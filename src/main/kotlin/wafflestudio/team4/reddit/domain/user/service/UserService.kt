package wafflestudio.team4.reddit.domain.user.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wafflestudio.team4.reddit.domain.follow.repository.FollowRepository
import wafflestudio.team4.reddit.domain.user.dto.UserDto
import wafflestudio.team4.reddit.domain.user.exception.UserProfileNotFoundException
import wafflestudio.team4.reddit.domain.user.exception.UnauthorizedSigninException
import wafflestudio.team4.reddit.domain.user.exception.UserNotFoundException
import wafflestudio.team4.reddit.domain.user.exception.UserDeletedException
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.model.UserImage
import wafflestudio.team4.reddit.domain.user.model.UserProfile
import wafflestudio.team4.reddit.domain.user.repository.UserImageRepository
import wafflestudio.team4.reddit.domain.user.repository.UserProfileRepository
import wafflestudio.team4.reddit.domain.user.repository.UserRepository
import java.util.Date

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
    private val userImageRepository: UserImageRepository,
    private val followRepository: FollowRepository,
    private val amazonS3: AmazonS3,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun signup(signupRequest: UserDto.SignupRequest): User {
        val encodedPassword = passwordEncoder.encode(signupRequest.password)
        // TODO deleted된 user와의 email uniqueness는 어떻게??

        val newUser = User(
            email = signupRequest.email,
            username = signupRequest.username,
            password = encodedPassword,
        )
        val newUserProfile = UserProfile(
            newUser,
        )
        val newUserImage = UserImage(
            newUserProfile,
        )
        newUserProfile.userImage = newUserImage
        newUser.userProfile = newUserProfile
        return userRepository.save(newUser)
    }

    fun signin(signinRequest: UserDto.SigninRequest): User {
        return userRepository.findByEmailAndPassword(
            signinRequest.email,
            passwordEncoder.encode(signinRequest.password)
        ) ?: throw UnauthorizedSigninException()
    }

    fun getUserById(userId: Long): User {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        if (user.deleted) throw UserDeletedException()
        return user
    }

    fun getUsersPage(lastUserId: Long, size: Int): Page<User> {
        val pageRequest = PageRequest.of(0, size)
        return userRepository.findByIdLessThanOrderByIdDesc(lastUserId, pageRequest)
    }

    fun updateUser(user: User, updateRequest: UserDto.UpdateRequest): User {
        val newEncodedPassword =
            if (updateRequest.password != null) passwordEncoder.encode(updateRequest.password) else null
        val updatedUser = user.updatedBy(updateRequest, newEncodedPassword)
        return userRepository.save(updatedUser)
    }

    fun deleteUser(user: User) {
        user.deleted = true
        userRepository.save(user)
    }

    // profile services
    fun getProfileById(id: Long): UserProfile {
        return userProfileRepository.findByIdOrNull(id) ?: throw UserProfileNotFoundException()
    }

    fun getFollowNumById(id: Long): Int {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()
        return followRepository.findByToUser(user).count()
    }

    fun getPresignedUrlAndSaveImage(user: User, fileName: String): String {
        val expiration: Date = Date()
        var expTimeMillis = expiration.time
        expTimeMillis += (1000 * 60 * 60).toLong() // 1시간
        expiration.time = expTimeMillis

        val request = GeneratePresignedUrlRequest("waffle-team-4-server-s3", "profiles/${user.id}/$fileName")
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration)

        val userImage = userImageRepository.findByUserProfile(user.userProfile!!)
            ?: throw UserProfileNotFoundException()
        userImage.url = "https://waffle-team-4-server-s3.s3.ap-northeast-2.amazonaws.com/profiles/" +
            "${user.id}/$fileName"
        userImageRepository.save(userImage)

        return amazonS3.generatePresignedUrl(request).toString()
    }

    fun updateProfile(user: User, updateRequest: UserDto.UpdateProfileRequest): UserProfile {
        if (user.userProfile == null) {
            // old user
            val newUserProfile = UserProfile(
                user,
            )
            val newUserImage = UserImage(
                newUserProfile,
            )
            newUserProfile.userImage = newUserImage
            user.userProfile = newUserProfile
            return userRepository.save(user).userProfile!!
        } else {
            var profile = user.userProfile!!
            profile.name = updateRequest.name
            profile.description = updateRequest.description
            return userProfileRepository.save(profile)
        }
    }
}
