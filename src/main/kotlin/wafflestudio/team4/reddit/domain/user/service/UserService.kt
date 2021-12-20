package wafflestudio.team4.reddit.domain.user.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wafflestudio.team4.reddit.domain.user.dto.UserDto
import wafflestudio.team4.reddit.domain.user.exception.UnauthorizedSigninException
import wafflestudio.team4.reddit.domain.user.exception.UserNotFoundException
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
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
        return userRepository.save(newUser)
    }

    fun signin(signinRequest: UserDto.SigninRequest): User {
        return userRepository.findByEmailAndPassword(
            signinRequest.email,
            passwordEncoder.encode(signinRequest.password)
        ) ?: throw UnauthorizedSigninException()
    }

    fun getUserById(userId: Long): User {
        return userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
    }

    fun updateUser(user: User, updateRequest: UserDto.UpdateRequest): User {
        val newEncodedPassword =
            if (updateRequest.password != null) passwordEncoder.encode(updateRequest.password) else null
        val updatedUser = user.updatedBy(updateRequest, newEncodedPassword)
        return userRepository.save(updatedUser)
    }

    fun deleteUser(user: User) {
        user.isDeleted = true
        userRepository.save(user)
    }
}
