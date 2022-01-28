package wafflestudio.team4.reddit.domain.post.api

import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping

import wafflestudio.team4.reddit.domain.post.dto.PostDto
import wafflestudio.team4.reddit.domain.post.service.PostService
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.service.UserService
import wafflestudio.team4.reddit.global.auth.annotation.CurrentUser
import wafflestudio.team4.reddit.global.common.dto.ListResponse
import wafflestudio.team4.reddit.global.common.dto.PageLinkDto
import wafflestudio.team4.reddit.global.common.dto.PageResponse
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postService: PostService,
    private val userService: UserService,
) {
    @GetMapping("/")
    fun getPostsPage(
//      @RequestParam(defaultValue = "new", name = "order") order: String,
        @RequestParam(name = "lastPostId", defaultValue = Long.MAX_VALUE.toString()) lastPostId: Long, // 현재 페이지
        @RequestParam(name = "size", defaultValue = "10") size: Int, // 각 페이지 당 게시글 수
        @RequestParam(required = false) keyword: String?,
    ): PageResponse<PostDto.Response> {
        val postsPage = postService.getPostsPage(lastPostId, size, keyword)
        val postLinks = buildPageLink(lastPostId, size, keyword)
        return PageResponse(postsPage.map { PostDto.Response(it) }, postLinks)
    }

    @GetMapping("/title/")
    fun getPostNamesPage(
//      @RequestParam(defaultValue = "new", name = "order") order: String,
        @RequestParam(name = "lastPostId", defaultValue = Long.MAX_VALUE.toString()) lastPostId: Long, // 현재 페이지
        @RequestParam(name = "size", defaultValue = "10") size: Int, // 각 페이지 당 게시글 수
        @RequestParam(required = false) keyword: String?,
    ): PageResponse<PostDto.PostNameResponse> {
        val postsPage = postService.getPostsPage(lastPostId, size, keyword)
        val postLinks = buildPageLink(lastPostId, size, keyword)
        return PageResponse(postsPage.map { PostDto.PostNameResponse(it) }, postLinks)
    }

    private fun buildPageLink(lastPostId: Long, size: Int, keyword: String?): PageLinkDto {
        // TODO refactor
        val first = "size=$size"
        val self = "lastPostId=$lastPostId&size=$size"
        val last = "lastPostId=${size + 1}&size=$size"

        val next = "lastPostId=${java.lang.Long.max(0, lastPostId - size)}&size=$size"
        val prev = "lastPostId=" +
            "${if ((lastPostId - Long.MAX_VALUE) + size > 0) Long.MAX_VALUE else lastPostId + size}&size=$size"

        return if (keyword == null) {
            PageLinkDto(first, prev, self, next, last)
        } else {
            PageLinkDto(
                "$first&keyword=$keyword", "$prev&keyword=$keyword",
                "$self&keyword=$keyword", "$next&keyword=$keyword", "$last&keyword=$keyword"
            )
        }
    }

    @GetMapping("/popular/")
    fun getPostsByPopularity(
        @RequestParam(name = "lastPostId", defaultValue = Long.MAX_VALUE.toString()) lastPostId: Long,
        @RequestParam(name = "size", defaultValue = "10") size: Int
    ): ListResponse<PostDto.Response> {
        val posts = postService.getPostsByPopularity(lastPostId, size)
        return ListResponse(posts.map { PostDto.Response(it) })
    }

    @GetMapping("/{post_id}/")
    fun getPost(
        @PathVariable("post_id") id: Long,
    ): PostDto.Response {
        val post = postService.getPostById(id)
        return PostDto.Response(post)
    }

    @GetMapping("/image/")
    fun getImageUploadUrl(
        @CurrentUser user: User,
        @Valid @RequestBody uploadImageRequest: PostDto.UploadImageRequest // TODO 이것도 param
    ): PostDto.UploadImageResponse {
        val mergedUser = userService.mergeUser(user)
        val preSignedUrl = postService.getPresignedUrl(mergedUser, uploadImageRequest.filename)
        val imageUrl = "https://waffle-team-4-server-s3.s3.ap-northeast-2.amazonaws.com/posts/" +
            "${mergedUser.id}/${uploadImageRequest.filename}"
        return PostDto.UploadImageResponse(preSignedUrl, imageUrl)
    }

    @PostMapping("/")
    @Transactional
    fun createPost(
        @CurrentUser user: User,
        @Valid @RequestBody createRequest: PostDto.CreateRequest
    ): ResponseEntity<PostDto.Response> {
        val mergedUser = userService.mergeUser(user)
        val newPost = postService.createPost(mergedUser, createRequest)
        return ResponseEntity.status(201).body(PostDto.Response(newPost))
    }

    @DeleteMapping("/{post_id}/")
    @Transactional
    fun deletePost(
        @CurrentUser user: User,
        @PathVariable("post_id") id: Long
    ): ResponseEntity<String> {
        val mergedUser = userService.mergeUser(user)
        postService.deletePost(mergedUser, id)
        return ResponseEntity.noContent().build()
    }

//    @PutMapping("/{post_id}/")
//    fun modifyPost(
//        @CurrentUser user: User,
//        @PathVariable("post_id") id: Long,
//        @Valid @RequestBody modifyRequest: PostDto.CreateRequest
//    ): PostDto.Response {
//        val modifiedPost = postService.modifyPost(user, id, modifyRequest)
//        return PostDto.Response(modifiedPost)
//    }

    @PutMapping("/{post_id}/vote/")
    @Transactional
    fun votePost(
        @CurrentUser user: User,
        @PathVariable("post_id") id: Long,
        @RequestParam(name = "isUp", required = true) isUp: Int,
    ): PostDto.Response {
        val mergedUser = userService.mergeUser(user)
        return PostDto.Response(postService.vote(mergedUser, id, isUp))
    }
}
