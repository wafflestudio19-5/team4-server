package wafflestudio.team4.reddit.domain.community.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import wafflestudio.team4.reddit.domain.community.dto.CommunityDto
import javax.validation.Valid
import wafflestudio.team4.reddit.domain.community.service.CommunityService
import wafflestudio.team4.reddit.domain.post.dto.PostDto
import wafflestudio.team4.reddit.domain.topic.dto.TopicDto
// import wafflestudio.team4.reddit.domain.topic.model.Topic
import wafflestudio.team4.reddit.domain.user.dto.UserDto
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.global.common.dto.ListResponse
import wafflestudio.team4.reddit.global.auth.annotation.CurrentUser
import wafflestudio.team4.reddit.global.common.dto.PageLinkDto
// import wafflestudio.team4.reddit.global.common.dto.PageLinkDto
import wafflestudio.team4.reddit.global.common.dto.PageResponse

@RestController
@RequestMapping("/api/v1/communities")
class CommunityController(
    private val communityService: CommunityService
) {
    // get community list
    @GetMapping("/")
    fun getCommunitiesPage(
        @RequestParam(required = false, defaultValue = Long.MAX_VALUE.toString()) lastCommunityId: Long,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "-1") topicId: Long,
        @RequestParam(required = false) keyword: String?
    ): PageResponse<CommunityDto.Response> {
        val communitiesPage = communityService.getCommunitiesPage(lastCommunityId, size, topicId, keyword)
        val communityLinks = buildPageLink(lastCommunityId, size, topicId, keyword)
        return PageResponse(communitiesPage.map { CommunityDto.Response(it) }, communityLinks)
    }

    private fun buildPageLink(lastCommunityId: Long, size: Int, topicId: Long, keyword: String?): PageLinkDto {
        val linkIds = communityService.getCommunityLinkIds(lastCommunityId, size, topicId, keyword)
        val firstId = linkIds[0]
        val lastId = linkIds[1]
        val nextId = linkIds[2]
        val prevId = linkIds[3]

        val first = if (keyword != null) "lastCommunityId=$firstId&size=$size&topicId=$topicId&keyword=$keyword"
        else "lastCommunityId=$firstId&size=$size&topicId=$topicId"
        val self = if (keyword != null) "lastCommunityId=$lastCommunityId&size=$size&topicId=$topicId&keyword=$keyword"
        else "lastCommunityId=$lastCommunityId&size=$size&topicId=$topicId"
        val last = if (keyword != null) "lastCommunityId=$lastId&size=$size&topicId=$topicId&keyword=$keyword"
        else "lastCommunityId=$lastId&size=$size&topicId=$topicId"
        val next = if (keyword != null) "lastCommunityId=$nextId&size=$size&topicId=$topicId&keyword=$keyword"
        else "lastCommunityId=$nextId&size=$size&topicId=$topicId"
        val prev = if (keyword != null) "lastCommunityId=$prevId&size=$size&topicId=$topicId&keyword=$keyword"
        else "lastCommunityId=$prevId&size=$size&topicId=$topicId"

        return PageLinkDto(first, prev, self, next, last)
    }

    // get community by id
    @GetMapping("/{community_id}/")
    fun getCommunityById(@PathVariable("community_id") communityId: Long): ResponseEntity<CommunityDto.Response> {
        val community = communityService.getCommunityById(communityId)
        return ResponseEntity.status(200).body(CommunityDto.Response(community))
    }

    @GetMapping("/{community_id}/posts/")
    fun getPostList(
        @PathVariable("community_id") communityId: Long,
        @RequestParam(required = false, defaultValue = Long.MAX_VALUE.toString()) lastPostId: Long,
        @RequestParam(required = false, defaultValue = "10") size: Int,
    ): ListResponse<PostDto.Response> {
        val posts = communityService.getCommunityPosts(communityId, lastPostId, size)
        return ListResponse(posts.map { PostDto.Response(it) })
    }

    @GetMapping("/{community_id}/about/moderators/")
    fun getManagers(@PathVariable("community_id") communityId: Long): ListResponse<UserDto.Response> {
        val managers = communityService.getManagers(communityId)
        return ListResponse(managers.map { UserDto.Response(it) })
    }

    @GetMapping("/{community_id}/about/topics/")
    fun getCommunityTopics(@PathVariable("community_id") communityId: Long): ListResponse<TopicDto.Response> {
        val topics = communityService.getTopics(communityId)
        return ListResponse(topics.map { TopicDto.Response(it) })
    }

    @GetMapping("/{community_id}/about/description/")
    fun getDescription(@PathVariable("community_id") communityId: Long):
        ResponseEntity<CommunityDto.Description> {
        val description = communityService.getDescription(communityId)
        return ResponseEntity.status(200).body(CommunityDto.Description(description))
    }

    // create community
    // anyone allowed to make community
    @PostMapping("/")
    fun createCommunity(@CurrentUser user: User, @Valid @RequestBody createRequest: CommunityDto.CreateRequest):
        ResponseEntity<CommunityDto.Response> {
        val community = communityService.createCommunity(createRequest, user)
        return ResponseEntity.status(201).body(CommunityDto.Response(community))
    }

    // join community
    @PostMapping("/{community_id}/me/")
    fun joinCommunity(
        @CurrentUser user: User,
        @PathVariable("community_id") communityId: Long,
        // @Valid @RequestBody joinRequest: CommunityDto.JoinRequest
    ): ResponseEntity<CommunityDto.Response> {
        // val role = joinRequest.role // role: manager, member
        val community = communityService.joinCommunity(user, communityId)
        return ResponseEntity.status(200).body(CommunityDto.Response(community))
    }

    // withdraw from community
    @DeleteMapping("/{community_id}/me/")
    fun leaveCommunity(
        @CurrentUser user: User,
        @PathVariable("community_id") communityId: Long
    ): ResponseEntity<CommunityDto.Response> {
        val community = communityService.leaveCommunity(user, communityId)
        return ResponseEntity.status(200).body(CommunityDto.Response(community))
    }

    // change community info
    // only this community's managers allowed to change info

    @PutMapping("/{community_id}/about/")
    fun modifyCommunity(
        @CurrentUser user: User,
        @PathVariable("community_id") communityId: Long,
        @Valid @RequestBody modifyRequest: CommunityDto.ModifyRequest
    ): ResponseEntity<CommunityDto.Response> {
        val community = communityService.modifyCommunity(user, modifyRequest, communityId)
        return ResponseEntity.status(200).body(CommunityDto.Response(community))
    }

    // 1) change description
    @PutMapping("/{community_id}/about/description/")
    fun modifyCommunityDescription(
        @CurrentUser user: User,
        @PathVariable("community_id") communityId: Long,
        @Valid @RequestBody modifyRequest: CommunityDto.ModifyDescriptionRequest
    ): ResponseEntity<CommunityDto.Response> {
        val community = communityService.modifyCommunityDescription(user, modifyRequest, communityId)
        return ResponseEntity.status(200).body(CommunityDto.Response(community))
    }

    // 2) add, delete manager
    @PostMapping("/{community_id}/about/moderators/{user_id}/")
    fun addCommunityManager(
        @CurrentUser user: User,
        @PathVariable("community_id") communityId: Long,
        @PathVariable("user_id") userId: Long
    ): ResponseEntity<CommunityDto.Response> {
        val community = communityService.addCommunityManager(user, communityId, userId)
        return ResponseEntity.status(200).body(CommunityDto.Response(community))
    }

    @DeleteMapping("/{community_id}/about/moderators/{user_id}/")
    fun deleteCommunityManager(
        @CurrentUser user: User,
        @PathVariable("community_id") communityId: Long,
        @PathVariable("user_id") userId: Long
    ): ResponseEntity<CommunityDto.Response> {
        val community = communityService.deleteCommunityManager(user, communityId, userId)
        return ResponseEntity.status(200).body(CommunityDto.Response(community))
    }

    // 3) add, delete topic (toggle)
    @PutMapping("/{community_id}/about/topics/{topic_id}/")
    fun changeCommunityTopic(
        @CurrentUser user: User,
        @PathVariable("community_id") communityId: Long,
        @PathVariable("topic_id") topicId: Long
    ): ResponseEntity<CommunityDto.Response> {
        val community = communityService.changeCommunityTopic(user, communityId, topicId)
        return ResponseEntity.status(200).body(CommunityDto.Response(community))
    }

    // delete community
    // only this community's managers allowed to delete
    @DeleteMapping("/{community_id}/")
    fun deleteCommunity(@CurrentUser user: User, @PathVariable("community_id") communityId: Long):
        ResponseEntity<Any> {
        val community = communityService.deleteCommunity(user, communityId)
        // what to return?
        return ResponseEntity.status(200).body(CommunityDto.Response(community))
    }
}
