package wafflestudio.team4.reddit.global.oauth.service

import org.springframework.core.ParameterizedTypeReference
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.model.UserImage
import wafflestudio.team4.reddit.domain.user.model.UserProfile
import wafflestudio.team4.reddit.domain.user.repository.UserProfileRepository
import wafflestudio.team4.reddit.domain.user.repository.UserRepository
import wafflestudio.team4.reddit.global.oauth.exception.InvalidOAuthProviderException
import wafflestudio.team4.reddit.global.oauth.exception.InvalidUserInfoAccessException
import wafflestudio.team4.reddit.global.oauth.info.OAuth2UserInfo
import wafflestudio.team4.reddit.global.oauth.info.OAuth2UserInfoFactory
import wafflestudio.team4.reddit.global.oauth.info.ProviderType
import wafflestudio.team4.reddit.global.oauth.provider.OAuthProvider
import wafflestudio.team4.reddit.global.oauth.repository.InMemoryProviderRepository

@Service
class OAuthService(
    private val inMemoryProviderRepository: InMemoryProviderRepository,
    private val userProfileRepository: UserProfileRepository,
    private val userRepository: UserRepository,
) {
    private final val oAuth2UserPassword = "oAuth2UserPassword"

    fun signinWithToken(providerName: String, accessToken: String): User {
        val provider = inMemoryProviderRepository.findByProviderName(providerName)
            ?: throw InvalidOAuthProviderException()

        val oAuth2UserInfo = getOAuth2UserInfoWithToken(providerName, accessToken, provider)
        return saveOrUpdate(oAuth2UserInfo)
    }

//    fun signin(providerName: String, code: String): User {
//        val provider = inMemoryProviderRepository.findByProviderName(providerName)
//            ?: throw InvalidOAuthProviderException()
//
//        // access token 가져오기
//        val tokenResponse = getToken(code, provider)
//
//        // user info 가져오기
//        val oAuth2UserInfo = getOAuth2UserInfo(providerName, tokenResponse, provider)
//
//        // user 저장
//        return saveOrUpdate(oAuth2UserInfo)
//    }

//    private fun getToken(code: String, provider: OAuthProvider): OAuthTokenResponse {
//        return WebClient.create()
//            .post()
//            .uri(provider.tokenUri)
//            .headers {
//                header ->
//                header.setBasicAuth(provider.clientId, provider.clientSecret)
//                header.contentType = MediaType.APPLICATION_FORM_URLENCODED
//                header.accept = Collections.singletonList(MediaType.APPLICATION_JSON)
//                header.acceptCharset = Collections.singletonList(StandardCharsets.UTF_8)
//            }
//            .bodyValue(tokenRequest(code, provider))
//            .retrieve()
//            .bodyToMono(OAuthTokenResponse::class.java)
//            .block() ?: throw InvalidTokenAccessException()
//    }

    private fun tokenRequest(code: String, provider: OAuthProvider): MultiValueMap<String, String> {
        val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
        formData.add("code", code)
        formData.add("grant_type", "authorization_code")
        formData.add("redirect_uri", provider.redirectUri)
        return formData
    }

//    private fun getOAuth2UserInfo(
//        providerName: String,
//        tokenResponse: OAuthTokenResponse,
//        provider: OAuthProvider
//    ): OAuth2UserInfo {
//        val providerType = ProviderType.valueOf(providerName.uppercase())
//        val userAttributes = getUserAttributes(provider, tokenResponse)
//        return OAuth2UserInfoFactory.extractFrom(providerType, userAttributes)
//    }

    private fun getOAuth2UserInfoWithToken(
        providerName: String,
        accessToken: String,
        provider: OAuthProvider,
    ): OAuth2UserInfo {
        val providerType = ProviderType.valueOf(providerName.uppercase())
        val userAttributes = getUserAttributesWithToken(provider, accessToken)
        return OAuth2UserInfoFactory.extractFrom(providerType, userAttributes)
    }

//    private fun getUserAttributes(provider: OAuthProvider, tokenResponse: OAuthTokenResponse): Map<String, Any> {
//        return WebClient.create()
//            .get()
//            .uri(provider.userInfoUri)
//            .headers { header -> header.setBearerAuth(tokenResponse.accessToken) }
//            .retrieve()
//            .bodyToMono(object : ParameterizedTypeReference<Map<String, Any>>() {})
//            .block() ?: throw InvalidUserInfoAccessException()
//    }

    private fun getUserAttributesWithToken(provider: OAuthProvider, accessToken: String): Map<String, Any> {
        return WebClient.create() // TODO not 200
            .get()
            .uri(provider.userInfoUri)
            .headers { header -> header.setBearerAuth(accessToken) }
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<Map<String, Any>>() {})
            .block() ?: throw InvalidUserInfoAccessException()
    }

    private fun saveOrUpdate(oAuth2UserInfo: OAuth2UserInfo): User {
        val user = userProfileRepository.findByOauthId(oAuth2UserInfo.id)?.user
        if (user != null) {
            // update
            val updatedUser = user.updatedBy(oAuth2UserInfo)
            updatedUser.userProfile?.userImage?.url = oAuth2UserInfo.imageUrl
            return userRepository.save(updatedUser)
        } else {
            // save
            val newUser = User(
                oAuth2UserInfo.email,
                oAuth2UserInfo.name,
                BCryptPasswordEncoder().encode(oAuth2UserPassword), // TODO DI
            )
            val newUserProfile = UserProfile(
                newUser,
                oAuth2UserInfo.id,
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
}
