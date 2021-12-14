package wafflestudio.team4.reddit.global.common.exception

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RedditControllerAdvice {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)
}
