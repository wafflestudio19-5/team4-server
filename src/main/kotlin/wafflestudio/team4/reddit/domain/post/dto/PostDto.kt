package wafflestudio.team4.reddit.domain.post.dto

import com.fasterxml.jackson.annotation.JsonProperty
import wafflestudio.team4.reddit.domain.post.model.Post
import wafflestudio.team4.reddit.domain.user.dto.UserDto
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

class PostDto {
    data class Response(
        val id: Long,
        @JsonProperty("user_id")
        val userId: Long,
        val community: String,
        @JsonProperty("user_name")
        val userName: String,
        // val userImageUrl: String?,
        @JsonProperty("profile")
        val userProfile: UserDto.ProfileResponse?,

        // Post contents
        val title: String,
        val text: String?,
        val images: List<String>?, // S3 이미지 주소

        // votes
        @JsonProperty("num_up_votes")
        val numUpVotes: Int,
        @JsonProperty("num_down_votes")
        val numDownVotes: Int,

        @JsonProperty("create_at")
        val createdAt: LocalDateTime?
        // val isDeleted: Boolean,
    ) {
        constructor(post: Post) : this(
            id = post.id,
            userId = post.user.id,
            community = post.community.name,
            userName = post.user.username,
            userProfile = if (post.user.userProfile != null) UserDto.ProfileResponse(post.user.userProfile!!) else null,
            title = post.title,
            text = post.text,
            images = post.images?.map { it.url },
            numUpVotes = post.votes.count { it.isUp == 2 },
            numDownVotes = post.votes.count { it.isUp == 0 },
            createdAt = post.createdAt
            // isDeleted = post.deleted
        )
    }

    data class CreateRequest(
        @field:NotBlank
        val community: String,

        @field:NotBlank
        val title: String,

        val text: String? = "",

        val images: List<String>? = null,

    )

    data class UploadImageRequest(
        @field:NotBlank
        val filename: String
    )

    data class UploadImageResponse(
        @JsonProperty("presigned_url")
        val preSignedUrl: String,
        @JsonProperty("image_url")
        val imageUrl: String,
    )
}
