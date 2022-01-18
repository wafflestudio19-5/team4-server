package wafflestudio.team4.reddit.domain.user.dto

import com.fasterxml.jackson.annotation.JsonProperty
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.model.UserProfile
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

    data class UsernameResponse(
        val id: Long,
        val username: String,
        @JsonProperty("date_joined")
        val dateJoined: LocalDateTime?,
    ) {
        constructor(user: User) : this(
            id = user.id,
            username = user.username,
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

    data class SigninRequest(
        @field:NotBlank
        @field:Email
        val email: String,

        @field:NotBlank
        val password: String,
    )

    data class UpdateRequest(
        @field:UniqueEmail
        @field:Email
        val email: String?,

        val username: String?,

        val password: String?,
    )

    data class UploadImageRequest(
        @field:NotBlank
        val filename: String
    )

    data class UploadImageResponse(
        val preSignedUrl: String,
        val imageUrl: String,
    )

    data class UpdateProfileRequest(
        val name: String,
        val description: String,
    )

    data class ProfileResponse(
        val userId: Long,
        val name: String,
        val imageUrl: String?,
        val description: String,
        val followers: Int,
    ) {
        constructor(userProfile: UserProfile, followersNum: Int) : this(
            userId = userProfile.user.id,
            name = userProfile.name,
            imageUrl = userProfile.userImage?.url,
            description = userProfile.description,
            followers = followersNum // followRepository.findByToUser(toUser:user)
        )
    }
}
