package wafflestudio.team4.reddit.domain.comment.dto

import wafflestudio.team4.reddit.domain.comment.model.Comment
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class CommentDto {
    data class Response(
        val id: Long,
        val userId: Long,
        val userName: String,
        val userImageUrl: String?,
        val text: String,
        val depth: Int,
        val parentId: Long?,
        val groupId: Long?,
        val numUpVotes: Int,
        val numDownVotes: Int,
        val deleted: Boolean,
    ) {
        constructor(comment: Comment) : this(
            id = comment.id,
            userId = comment.user.id,
            userName = comment.user.username,
            userImageUrl = comment.user.userProfile!!.userImage?.url,
            text = comment.text,
            depth = comment.depth,
            parentId = comment.parent?.id,
            groupId = comment.group?.id,
            numUpVotes = comment.votes.count { it.isUp == 2 },
            numDownVotes = comment.votes.count { it.isUp == 0 },
            deleted = comment.deleted == 1 || comment.deleted == 2
        )
    }

    data class CreateRequest(

        @field:NotBlank
        val text: String,

        @field:NotNull
        val depth: Int,

        @field:NotNull
        val parentId: Long = 0L,

        @field:NotNull
        val groupId: Long = 0L,
    )

    data class ModifyRequest(
        @field:NotBlank
        val text: String,
    )
}
