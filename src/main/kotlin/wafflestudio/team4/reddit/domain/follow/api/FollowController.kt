package wafflestudio.team4.reddit.domain.follow.api

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wafflestudio.team4.reddit.domain.follow.service.FollowService

@RestController
@RequestMapping("/api/v1/follow")
class FollowController(
    val followService: FollowService
)
