package wafflestudio.team4.reddit.global.auth.model

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import wafflestudio.team4.reddit.domain.user.repository.UserRepository
import wafflestudio.team4.reddit.global.auth.dto.LoginRequest
import wafflestudio.team4.reddit.global.auth.exception.UnexpectedUserNotFoundException

@Service
class UserPrincipalDetailService(
    private val userRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(s: String): UserDetails {
        val user = userRepository.findByEmail(s) ?: throw UsernameNotFoundException("User with email '%s' not found")
        return UserPrincipal(user)
    }

    fun isDeletedUser(loginRequest: LoginRequest): Boolean {
        if (loginRequest.email == null) {
            return false
        }
        val user =
            userRepository.findByEmail(
                loginRequest.email,
            ) ?: throw UnexpectedUserNotFoundException()
        return user.isDeleted
    }
}
