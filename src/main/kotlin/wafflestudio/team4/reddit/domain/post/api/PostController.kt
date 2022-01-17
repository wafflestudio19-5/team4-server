package wafflestudio.team4.reddit.domain.post.api

import org.springframework.http.ResponseEntity
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
import wafflestudio.team4.reddit.global.auth.annotation.CurrentUser
import wafflestudio.team4.reddit.global.common.dto.ListResponse
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postService: PostService,
) {
    @GetMapping("/")
    fun getPosts(
//      @RequestParam(defaultValue = "new", name = "order") order: String,
        @RequestParam(name = "lastPostId", defaultValue = Long.MAX_VALUE.toString()) lastPostId: Long, // 현재 페이지
        @RequestParam(name = "size", defaultValue = "10") size: Int, // 각 페이지 당 게시글 수
    ): ListResponse<PostDto.Response> {
        val posts = postService.getPosts(lastPostId, size)
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
        val preSignedUrl = postService.getPresignedUrl(user, uploadImageRequest.filename)
        val imageUrl = "https://waffle-team-4-server-s3.s3.ap-northeast-2.amazonaws.com/posts/" +
            "${user.id}/${uploadImageRequest.filename}"
        return PostDto.UploadImageResponse(preSignedUrl, imageUrl)
    }

    @PostMapping("/")
    fun createPost(
        @CurrentUser user: User,
        @Valid @RequestBody createRequest: PostDto.CreateRequest
    ): ResponseEntity<PostDto.Response> {
        val newPost = postService.createPost(user, createRequest)
        return ResponseEntity.status(201).body(PostDto.Response(newPost))
    }

    @DeleteMapping("/{post_id}/")
    fun deletePost(
        @CurrentUser user: User,
        @PathVariable("post_id") id: Long
    ): ResponseEntity<String> {
        postService.deletePost(user, id)
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
    fun votePost(
        @CurrentUser user: User,
        @PathVariable("post_id") id: Long,
        @RequestParam(name = "isUp", required = true) isUp: Int,
    ): PostDto.Response {
        return PostDto.Response(postService.vote(user, id, isUp))
    }
}
