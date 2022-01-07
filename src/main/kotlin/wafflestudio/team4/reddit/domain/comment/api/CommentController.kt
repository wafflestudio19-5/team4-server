package wafflestudio.team4.reddit.domain.comment.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestBody

import wafflestudio.team4.reddit.domain.comment.dto.CommentDto
import wafflestudio.team4.reddit.domain.comment.service.CommentService
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.global.auth.annotation.CurrentUser
import wafflestudio.team4.reddit.global.common.dto.ListResponse
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/comments")
class CommentController(
    private val commentService: CommentService
) {
    @GetMapping("/{post_id}/")
    fun getComments(
        @PathVariable("post_id") postId: Long,
        @RequestParam(name = "lastCommentId", defaultValue = Long.MAX_VALUE.toString()) lastCommentId: Long,
        @RequestParam(name = "size", defaultValue = "15") size: Int
    ): ListResponse<CommentDto.Response> {
        val comments = commentService.getComments(lastCommentId, size, postId)
        return ListResponse(comments.map { CommentDto.Response(it) })
    }

    @PostMapping("/{post_id}/")
    fun createComment(
        @CurrentUser user: User,
        @PathVariable("post_id") postId: Long,
        @Valid @RequestBody createRequest: CommentDto.CreateRequest,
    ): ResponseEntity<CommentDto.Response> {
        val newComment = commentService.createComment(user, postId, createRequest)
        return ResponseEntity.status(201).body(CommentDto.Response(newComment))
    }

    @DeleteMapping("/{comment_id}/")
    fun deleteComment(
        @CurrentUser user: User,
        @PathVariable("comment_id") commentId: Long,
    ): ResponseEntity<String> {
        commentService.deleteComment(user, commentId)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{comment_id}/")
    fun modifyComment(
        @CurrentUser user: User,
        @PathVariable("comment_id") commentId: Long,
        @Valid @RequestBody modifyRequest: CommentDto.ModifyRequest
    ): CommentDto.Response {
        val modifiedComment = commentService.modifyComment(user, commentId, modifyRequest)
        return CommentDto.Response(modifiedComment)
    }

    @PutMapping("/{comment_id}/vote/")
    fun voteComment(
        @CurrentUser user: User,
        @PathVariable("comment_id") commentId: Long,
        @RequestParam(name = "isUp", required = true) isUp: Int,
    ): CommentDto.Response {
        return CommentDto.Response(commentService.voteComment(user, commentId, isUp))
    }
}
