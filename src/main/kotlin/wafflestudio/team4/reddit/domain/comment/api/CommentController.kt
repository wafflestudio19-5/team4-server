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
import wafflestudio.team4.reddit.global.common.dto.PageResponse
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
    ): PageResponse<CommentDto.Response> {
        val comments = commentService.getComments(lastCommentId, size, postId)
        return PageResponse(comments.map { CommentDto.Response(it) }, comments.size, comments.size, null)
    }

    @GetMapping("/{post_id}/popular/")
    fun getCommentsByPopularity(
        @PathVariable("post_id") postId: Long,
        @RequestParam(name = "lastCommentId", defaultValue = Long.MAX_VALUE.toString()) lastCommentId: Long,
        @RequestParam(name = "size", defaultValue = "15") size: Int
    ): PageResponse<CommentDto.Response> {
        val comments = commentService.getCommentsByPopularity(lastCommentId, size, postId)
        return PageResponse(comments.map { CommentDto.Response(it) }, comments.size, comments.size, null)
    }

    @GetMapping("/comment/{comment_id}/")
    fun getCommentById(
        @PathVariable("comment_id") commentId: Long
    ): CommentDto.Response {
        val comment = commentService.getCommentById(commentId)
        return CommentDto.Response(comment)
    }

    @PostMapping("/{post_id}/")
    fun createComment(
        @CurrentUser user: User,
        @PathVariable("post_id") postId: Long,
        @Valid @RequestBody createRequest: CommentDto.CreateRequest,
    ): ResponseEntity<CommentDto.Response> {
        val mergedUser = commentService.mergeUser(user)
        val newComment = commentService.createComment(mergedUser, postId, createRequest)
        return ResponseEntity.status(201).body(CommentDto.Response(newComment))
    }

    @PostMapping("/{post_id}/{comment_id}/reply/")
    fun replyComment(
        @CurrentUser user: User,
        @PathVariable("post_id") postId: Long,
        @PathVariable("comment_id") commentId: Long,
        @Valid @RequestBody replyRequest: CommentDto.ReplyRequest,
    ): ResponseEntity<CommentDto.Response> {
        val mergedUser = commentService.mergeUser(user)
        val newReplyComment = commentService.replyComment(mergedUser, postId, commentId, replyRequest)
        return ResponseEntity.status(201).body(CommentDto.Response(newReplyComment))
    }

    @DeleteMapping("/{comment_id}/")
    fun deleteComment(
        @CurrentUser user: User,
        @PathVariable("comment_id") commentId: Long,
    ): ResponseEntity<String> {
        val mergedUser = commentService.mergeUser(user)
        commentService.deleteComment(mergedUser, commentId)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{comment_id}/")
    fun modifyComment(
        @CurrentUser user: User,
        @PathVariable("comment_id") commentId: Long,
        @Valid @RequestBody modifyRequest: CommentDto.ModifyRequest
    ): CommentDto.Response {
        val mergedUser = commentService.mergeUser(user)
        val modifiedComment = commentService.modifyComment(mergedUser, commentId, modifyRequest)
        return CommentDto.Response(modifiedComment)
    }

    @PutMapping("/{comment_id}/vote/")
    fun voteComment(
        @CurrentUser user: User,
        @PathVariable("comment_id") commentId: Long,
        @RequestParam(name = "isUp", required = true) isUp: Int,
    ): CommentDto.Response {
        val mergedUser = commentService.mergeUser(user)
        return CommentDto.Response(commentService.voteComment(mergedUser, commentId, isUp))
    }
}
