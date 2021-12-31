package wafflestudio.team4.reddit.domain.post.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import wafflestudio.team4.reddit.domain.post.dto.PostDto
import wafflestudio.team4.reddit.domain.post.service.PostService
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.global.auth.CurrentUser
import wafflestudio.team4.reddit.global.auth.JwtTokenProvider
import wafflestudio.team4.reddit.global.common.dto.ListResponse
import javax.validation.Valid


@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postService: PostService,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @GetMapping("/")
    fun getPosts(
//      @RequestParam(defaultValue = "new", name = "order") order: String,
        @RequestParam(name = "lastPostId", defaultValue = Long.MAX_VALUE.toString()) lastPostId : Long, // 현재 페이지
        @RequestParam(name = "size", defaultValue = "10") size: Int, // 각 페이지 당 게시글 수
    ): ListResponse<PostDto.Response> {
        val posts = postService.getPosts(lastPostId, size)
        return ListResponse(posts.map { PostDto.Response(it) } )
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
        @Valid @RequestBody uploadImageRequest: PostDto.UploadImageRequest
    ): PostDto.UploadImageResponse{
        val url = postService.getPresignedUrl(uploadImageRequest.filename)
        return PostDto.UploadImageResponse(url)
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
    ): PostDto.Response {
        val deletedPost = postService.deletePost(user, id)
        return PostDto.Response(deletedPost)
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
