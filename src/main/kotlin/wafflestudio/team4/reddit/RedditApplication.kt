package wafflestudio.team4.reddit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedditApplication

fun main(args: Array<String>) {
    runApplication<RedditApplication>(*args)
}
