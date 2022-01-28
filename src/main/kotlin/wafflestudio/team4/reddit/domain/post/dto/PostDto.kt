package wafflestudio.team4.reddit.domain.post.dto

import com.fasterxml.jackson.annotation.JsonProperty
import wafflestudio.team4.reddit.domain.post.model.Post
import wafflestudio.team4.reddit.domain.user.dto.UserDto
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

class PostDto {
    data class Response(
        val id: Long,
        val userId: Long,
        val community: String,
        val userName: String,
        val userImageUrl: String?,
        val title: String,
        val text: String?,
        val images: List<String>?, // S3 이미지 주소
        val numUpVotes: Int,
        val numDownVotes: Int,
        val createdAt: LocalDateTime?
//        val isDeleted: Boolean,
    ) {
        constructor(post: Post) : this(
            id = post.id,
            userId = post.user.id,
            community = post.community.name,
            userName = post.user.username,
            userImageUrl = post.user.userProfile?.userImage?.url,
            title = post.title,
            text = post.text,
            images = post.images?.map { it.url }, // S3적용 후 변경
            numUpVotes = post.votes.count { it.isUp == 2 },
            numDownVotes = post.votes.count { it.isUp == 0 },
            createdAt = post.createdAt
//            isDeleted = post.deleted
        )
    }

    data class PostNameResponse(
        val id: Long,
        val author: UserDto.Response,
        val title: String,
        @JsonProperty("created_at")
        val createdAt: LocalDateTime?,
    ) {
        constructor(post: Post) : this(
            id = post.id,
            author = UserDto.Response(post.user),
            title = post.title,
            createdAt = post.createdAt,
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
        val preSignedUrl: String,
        val imageUrl: String,
    )
}
