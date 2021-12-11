package wafflestudio.team4.reddit.domain.user.dto

import com.fasterxml.jackson.annotation.JsonProperty
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.global.validation.constraints.UniqueEmail
import java.time.LocalDateTime
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class UserDto {
    data class Response(
        val id: Long,
        val username: String,
        val email: String,
        @JsonProperty("date_joined")
        val dateJoined: LocalDateTime?,
    ) {
        constructor(user: User) : this(
            id = user.id,
            username = user.username,
            email = user.email,
            dateJoined = user.createdAt,
        )
    }

    data class SignupRequest(
        @field:UniqueEmail
        @field:NotBlank
        @field:Email
        val email: String,

        @field:NotBlank
        val username: String,

        @field:NotBlank
        val password: String,
    )
}
