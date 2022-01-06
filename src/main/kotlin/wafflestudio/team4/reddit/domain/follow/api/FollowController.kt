package wafflestudio.team4.reddit.domain.follow.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import wafflestudio.team4.reddit.domain.follow.dto.FollowDto
import wafflestudio.team4.reddit.domain.follow.service.FollowService
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.global.auth.CurrentUser

@RestController
@RequestMapping("/api/v1/follow")
class FollowController(
    val followService: FollowService
) {

    @PostMapping("/{toUser_id}/")
    fun follow(@CurrentUser fromUser: User, @PathVariable("toUser_id") toUserId: Long):
        ResponseEntity<FollowDto.Response> {
        val follow = followService.follow(fromUser, toUserId)
        return ResponseEntity.status(201).body(FollowDto.Response(follow))
    }

    @DeleteMapping("/{toUser_id}/")
    fun unfollow(@CurrentUser fromUser: User, @PathVariable("toUser_id") toUserId: Long):
        ResponseEntity<FollowDto.Response> {
        val follow = followService.unfollow(fromUser, toUserId)
        return ResponseEntity.status(200).body(FollowDto.Response(follow))
    }
}
