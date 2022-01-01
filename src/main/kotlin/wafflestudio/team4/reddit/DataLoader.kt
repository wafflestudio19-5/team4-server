package wafflestudio.team4.reddit

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import wafflestudio.team4.reddit.domain.topic.model.Topic
import wafflestudio.team4.reddit.domain.topic.repository.TopicRepository
// import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.repository.UserRepository

@Component
class DataLoader(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val topicRepository: TopicRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
//        for (i: Int in 1..100) {
//            val username = "user$i"
//            val user = User("$username@snu.ac.kr", username, passwordEncoder.encode("somepassword"))
//            userRepository.save(user)
//        }
//
        val topicList = listOf(
            "Animals and Pets",
            "Anime",
            "Beauty and Makeup",
            "Business, Economics, and Finance",
            "Cars and Motor Vehicles",
            "Celebrity",
            "Crypto",
            "Fashion",
            "Food and Drink",
            "Funny/Humor",
            "Gaming",
            "Hobbies",
            "Movies",
            "Music",
            "Programming",
            "Politics",
            "Technology",
            "World News"
        )
        for (topicName in topicList) {
            val topic = Topic(topicName)
            topicRepository.save(topic)
        }
    }
}
