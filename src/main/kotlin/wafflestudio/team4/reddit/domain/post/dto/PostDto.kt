package wafflestudio.team4.reddit.domain.post.dto

import wafflestudio.team4.reddit.domain.post.model.Post
import wafflestudio.team4.reddit.domain.user.model.User
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class PostDto {
    data class Response(
        val id: Long,
        val userId: Long,
        val title: String,
        val text: String?,
        val images: List<String>?, // S3 이미지 주소
        val numUpVotes: Int,
        val numDownVotes: Int,
//        val isDeleted: Boolean,
    ) {
        constructor(post: Post) : this(
            id = post.id,
            userId = post.user.id,
            title = post.title,
            text = post.text,
            images = post.images?.map { it.url }, // S3적용 후 변경
            numUpVotes = post.votes.count { it.isUp == 2 },
            numDownVotes = post.votes.count { it.isUp == 0 },
//            isDeleted = post.deleted
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
        val url: String,
    )

}
