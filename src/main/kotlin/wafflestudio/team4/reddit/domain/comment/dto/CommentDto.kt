package wafflestudio.team4.reddit.domain.comment.dto

import com.fasterxml.jackson.annotation.JsonProperty
import wafflestudio.team4.reddit.domain.comment.model.Comment
import wafflestudio.team4.reddit.domain.user.dto.UserDto
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

class CommentDto {
    data class Response(
        val id: Long,
        val author: UserDto.Response,
        val text: String,
        val depth: Int,
        @JsonProperty("root_comment_id")
        val rootCommentId: Long,
        @JsonProperty("parent_comment_id")
        val parentCommentId: Long?,
        @JsonProperty("children_comment_list")
        val childrenCommentList: List<Response>,
        @JsonProperty("num_up_votes")
        val numUpVotes: Int,
        @JsonProperty("num_down_votes")
        val numDownVotes: Int,
        val deleted: Boolean,
        @JsonProperty("created_at")
        val createdAt: LocalDateTime?
    ) {
        constructor(comment: Comment) : this(
            id = comment.id,
            author = UserDto.Response(comment.user),
            text = comment.text,
            depth = comment.depth,
            rootCommentId = comment.rootComment!!.id,
            parentCommentId = comment.parentComment?.id,
            childrenCommentList = comment.childrenComments.map { Response(it) },
            numUpVotes = comment.votes.count { it.isUp == 2 },
            numDownVotes = comment.votes.count { it.isUp == 0 },
            deleted = comment.deleted == 1 || comment.deleted == 2,
            createdAt = comment.createdAt
        )
    }

    data class CreateRequest(
        @field:NotBlank
        val text: String,
    )

    data class ReplyRequest(
        @field:NotBlank
        val text: String,
    )

    data class ModifyRequest(
        @field:NotBlank
        val text: String,
    )
}
