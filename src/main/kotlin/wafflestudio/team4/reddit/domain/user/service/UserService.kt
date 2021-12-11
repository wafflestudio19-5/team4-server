package wafflestudio.team4.reddit.domain.user.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wafflestudio.team4.reddit.domain.user.dto.UserDto
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun signup(signupRequest: UserDto.SignupRequest): User {
        // TODO
        return User("hi@hi", "hi", "asdf")
    }
}
