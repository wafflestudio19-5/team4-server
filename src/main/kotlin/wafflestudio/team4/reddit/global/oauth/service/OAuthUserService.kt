package wafflestudio.team4.reddit.global.oauth.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import wafflestudio.team4.reddit.domain.user.exception.UserAlreadyExistsException
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.model.UserImage
import wafflestudio.team4.reddit.domain.user.model.UserProfile
import wafflestudio.team4.reddit.domain.user.repository.UserImageRepository
import wafflestudio.team4.reddit.domain.user.repository.UserProfileRepository
import wafflestudio.team4.reddit.domain.user.repository.UserRepository
import wafflestudio.team4.reddit.global.auth.model.UserPrincipal
import wafflestudio.team4.reddit.global.oauth.exception.EmptyOAuth2UserRequestException
import wafflestudio.team4.reddit.global.oauth.info.OAuth2UserInfo
import wafflestudio.team4.reddit.global.oauth.info.OAuth2UserInfoFactory
import wafflestudio.team4.reddit.global.oauth.info.ProviderType

@Service
class OAuthUserService(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
    private val userImageRepository: UserImageRepository,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    // TODO OAuth2 user password?
    private final val oAuth2UserPassword = "oAuth2UserPassword"

    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        if (userRequest == null) throw EmptyOAuth2UserRequestException()

        val delegate = DefaultOAuth2UserService()
        val oAuth2User = delegate.loadUser(userRequest) // OAuth service에서 가져온 유저 정보 포함

        // oauth service name
        val registrationId = userRequest.clientRegistration.registrationId
        val providerType = ProviderType.valueOf(registrationId.uppercase())
        // login pk
        val userNameAttributeName =
            userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        // user info
        val attributes = oAuth2User.attributes

        val oAuth2UserInfo = OAuth2UserInfoFactory.extractFrom(providerType, attributes)
        val savedUser = saveOrUpdate(oAuth2UserInfo)

        return DefaultOAuth2User(
            UserPrincipal(savedUser).authorities,
            attributes,
            userNameAttributeName,
        )
    }

    private fun saveOrUpdate(oAuth2UserInfo: OAuth2UserInfo): User {
        // TODO exception handling: getId
        val user: User? = userProfileRepository.findByOauthId(oAuth2UserInfo.id)?.user
        val updatedUser = if (user != null) update(user, oAuth2UserInfo) else save(oAuth2UserInfo)
        return userRepository.save(updatedUser)
    }

    private fun update(user: User, oAuth2UserInfo: OAuth2UserInfo): User {
        val updatedUser = user.updatedBy(oAuth2UserInfo)
        updatedUser.userProfile?.userImage?.url = oAuth2UserInfo.imageUrl // TODO null일때 그냥 새로 만들어야 하나?
        return userRepository.save(updatedUser)
    }

    private fun save(oAuth2UserInfo: OAuth2UserInfo): User {
        if (userRepository.existsByEmail(oAuth2UserInfo.email)) throw UserAlreadyExistsException()
        val newUser = User(
            email = oAuth2UserInfo.email,
            username = oAuth2UserInfo.name,
            password = BCryptPasswordEncoder().encode(oAuth2UserPassword), // TODO DI
        )
        val newUserProfile = UserProfile(
            user = newUser,
            oauthId = oAuth2UserInfo.id
        )
        val newUserImage = UserImage(
            newUserProfile,
            oAuth2UserInfo.imageUrl
        )

        newUserProfile.userImage = newUserImage
        newUser.userProfile = newUserProfile
        return userRepository.save(newUser)
    }
}
