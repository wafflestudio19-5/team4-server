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
        val content: String,
        val imageUriList: List<String>,
//        val numUpVotes: Int,
//        val numDownVotes: Int,
    ) {
        constructor(post: Post) : this(
            id = post.id,
            userId = post.user.id,
            title = post.title,
            content = post.content,
            imageUriList = post.images.map { it.path }, // S3적용 후 변경
//            numUpVotes = post.votes.count { it.isUp },
//            numDownVotes = post.votes.count { !it.isUp },
        )
    }

    data class CreateRequest(
        @field:NotBlank
        val title: String,

        @field:NotBlank
        val content: String,

        // val images

    )

}
