package wafflestudio.team4.reddit.domain.follow.dto

import com.fasterxml.jackson.annotation.JsonProperty
import wafflestudio.team4.reddit.domain.follow.model.Follow
import wafflestudio.team4.reddit.domain.user.model.User
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class FollowDto {
    data class Response(
        val id: Long,
        @JsonProperty("from_user_name")
        @field:NotBlank
        val fromUserName: String,
        @JsonProperty("to_user_name")
        @field:NotBlank
        val toUserName: String,
        @field:NotNull
        val deleted: Boolean
    ) {
        constructor(follow: Follow) : this(
            id = follow.id,
            fromUserName = follow.fromUser.username,
            toUserName = follow.toUser.username,
            deleted = follow.deleted
        )
    }

    data class FollowerResponse(
        val id: Long,
        val username: String,
        val email: String,
    ) {
        constructor(user: User) : this(
            id = user.id,
            username = user.username,
            email = user.email
        )
    }
}
