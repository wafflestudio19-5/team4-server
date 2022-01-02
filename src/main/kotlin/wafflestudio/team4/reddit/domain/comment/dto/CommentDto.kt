package wafflestudio.team4.reddit.domain.comment.dto

import wafflestudio.team4.reddit.domain.comment.model.Comment
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class CommentDto {
    data class Response(
        val id: Long,
        val userId: Long,
        val text: String,
        val depth: Int,
        val parentId: Long,
        val numUpVotes: Int,
        val numDownVotes: Int,
        val deleted: Boolean,
    ) {
        constructor(comment: Comment) : this(
            id = comment.id,
            userId = comment.user.id,
            text = comment.text,
            depth = comment.depth,
            parentId = comment.parent.id,
            numUpVotes = comment.votes.count { it.isUp == 2 },
            numDownVotes = comment.votes.count { it.isUp == 0 },
            deleted = comment.deleted
        )
    }

    data class CreateRequest(
        @field:NotNull
        val postId: Long,

        @field:NotBlank
        val text: String,

        @field:NotNull
        val depth: Int,

        @field:NotNull
        val parentId: Long
    )

    data class ModifyRequest(
        @field:NotBlank
        val text: String,
    )
}
