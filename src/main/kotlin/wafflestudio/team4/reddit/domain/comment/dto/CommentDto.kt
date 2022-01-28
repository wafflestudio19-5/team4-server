package wafflestudio.team4.reddit.domain.comment.dto

import com.fasterxml.jackson.annotation.JsonProperty
import wafflestudio.team4.reddit.domain.comment.model.Comment
import wafflestudio.team4.reddit.domain.user.dto.UserDto
import wafflestudio.team4.reddit.domain.user.model.UserProfile
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class CommentDto {
    data class Response(
        val id: Long,
        @JsonProperty("user_id")
        val userId: Long,
        @JsonProperty("username")
        val username: String,
        @JsonProperty("user_profile")
        val userProfile: UserDto.ProfileResponse?,
        val text: String,

        // Parent and child comments
        val depth: Int,
        val parentId: Long?,
        val groupId: Long?,
        val numUpVotes: Int,
        val numDownVotes: Int,
        val deleted: Boolean,
        val createdAt: LocalDateTime?
    ) {
        constructor(comment: Comment) : this(
            id = comment.id,
            userId = comment.user.id,
            username = comment.user.username,
            userImageUrl = if (comment.user.userProfile != null) UserDto.ProfileResponse(comment.user.userProfile!!) else null,
            text = comment.text,
            depth = comment.depth,
            parentId = comment.parent?.id,
            groupId = comment.group?.id,
            numUpVotes = comment.votes.count { it.isUp == 2 },
            numDownVotes = comment.votes.count { it.isUp == 0 },
            deleted = comment.deleted == 1 || comment.deleted == 2,
            createdAt = comment.createdAt
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
