package wafflestudio.team4.reddit.domain.ping.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/ping")
class PingController {

    // api 명세 정해야댐
    @GetMapping("/")
    fun pong(): ResponseEntity<String> {
        return ResponseEntity.ok("pong!")
    }
}
