package wafflestudio.team4.reddit

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
// import wafflestudio.team4.reddit.domain.user.model.User
// import wafflestudio.team4.reddit.domain.user.model.UserImage
// import wafflestudio.team4.reddit.domain.user.model.UserProfile
import wafflestudio.team4.reddit.domain.user.repository.UserRepository

@Component
class DataLoader(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
//        for (i: Int in 1..10) {
//            val username = "user$i"
//            val user = User("$username@snu.ac.kr", username, passwordEncoder.encode("somepassword"))
//
//            val newUserProfile = UserProfile(
//                user,
//            )
//            val newUserImage = UserImage(
//                newUserProfile,
//            )
//            newUserProfile.userImage = newUserImage
//            user.userProfile = newUserProfile
//            userRepository.save(user)
//        }
//
    }
}
