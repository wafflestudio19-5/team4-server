package wafflestudio.team4.reddit

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.repository.UserRepository

@Component
class DataLoader(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        for (i: Int in 1..100) {
            val username = "user$i"
            val user = User("$username@snu.ac.kr", username, passwordEncoder.encode("somepassword"))
            userRepository.save(user)
        }
    }
}
