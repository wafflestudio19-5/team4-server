package wafflestudio.team4.reddit.domain.follow.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import wafflestudio.team4.reddit.domain.follow.dto.FollowDto
import wafflestudio.team4.reddit.domain.follow.service.FollowService
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.global.auth.CurrentUser
import wafflestudio.team4.reddit.global.common.dto.PageLinkDto
import wafflestudio.team4.reddit.global.common.dto.PageResponse

@RestController
@RequestMapping("/api/v1/follow")
class FollowController(
    val followService: FollowService
) {

    // get following?

    @GetMapping("/{fromUser_id}")
    fun getFollowersPage(
        @PathVariable("fromUser_id") fromUserId: Long,
        @RequestParam(required = false, defaultValue = Long.MAX_VALUE.toString()) lastFollowId: Long,
        @RequestParam(required = false, defaultValue = "10") size: Int,
    ): PageResponse<FollowDto.Response> {
        val followPage = followService.getFollowersPage(fromUserId, lastFollowId, size)
        val followLinks = buildPageLink(lastFollowId, size)
        return PageResponse(followPage.map { FollowDto.Response(it) }, followLinks)
    }

    private fun buildPageLink(lastFollowId: Long, size: Int): PageLinkDto {
        val first = "size=$size"
        val self = "lastFollowId=$lastFollowId&size=$size"
        val last = "lastFollowId=${size + 1}&size=$size"

        val next = "lastFollowId=${java.lang.Long.max(0, lastFollowId - size)}&size=$size"
        val prev = "lastFollowId=" +
            "${if ((lastFollowId - Long.MAX_VALUE) + size > 0)
                Long.MAX_VALUE else lastFollowId + size}&size=$size"

        return PageLinkDto(first, prev, self, next, last)
    }

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
