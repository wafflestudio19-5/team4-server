package wafflestudio.team4.reddit.domain.community.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import wafflestudio.team4.reddit.domain.community.dto.CommunityDto
import javax.validation.Valid
import wafflestudio.team4.reddit.domain.community.service.CommunityService
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.global.auth.annotation.CurrentUser
import wafflestudio.team4.reddit.global.common.dto.ListResponse

@RestController
@RequestMapping("/api/v1/communities")
class CommunityController(
    private val communityService: CommunityService
) {
    // get community list
    @GetMapping("/")
    fun getCommunities(): ResponseEntity<ListResponse<CommunityDto.Response>> { // TODO pagination needed
        val communities = communityService.getAllCommunities()
        // specify when request community list
        val response = ListResponse(communities.map { CommunityDto.Response(it) })
        return ResponseEntity.status(200).body(response)
    }

    // get community by id
    @GetMapping("/{community_id}/")
    fun getCommunityById(@PathVariable("community_id") communityId: Long): ResponseEntity<CommunityDto.Response> {
        val community = communityService.getCommunityById(communityId)
        return ResponseEntity.status(200).body(CommunityDto.Response(community))
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
        @Valid @RequestBody joinRequest: CommunityDto.JoinRequest
    ): ResponseEntity<CommunityDto.Response> {
        val role = joinRequest.role // role: manager, member
        val community = communityService.joinCommunity(user, communityId, role)
        return ResponseEntity.status(201).body(CommunityDto.Response(community))
    }

    // withdraw from community
    @DeleteMapping("/{community_id}/me/") // change URL pattern
    fun leaveCommunity(
        @CurrentUser user: User,
        @PathVariable("community_id") communityId: Long
    ): ResponseEntity<CommunityDto.Response> {
        val community = communityService.leaveCommunity(user, communityId)
        return ResponseEntity.status(200).body(CommunityDto.Response(community))
    }

    // change already existing community info
    // only this community's managers allowed to change info
    @PutMapping("/{community_id}/")
    fun modifyCommunity(
        @CurrentUser user: User,
        @PathVariable("community_id") communityId: Long,
        @Valid @RequestBody modifyRequest: CommunityDto.ModifyRequest
    ): ResponseEntity<CommunityDto.Response> {
        val community = communityService.modifyCommunity(user, modifyRequest, communityId)
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
