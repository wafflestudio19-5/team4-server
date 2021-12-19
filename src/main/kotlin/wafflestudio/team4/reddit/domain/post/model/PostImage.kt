package wafflestudio.team4.reddit.domain.post.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_post_image")
class PostImage(
    @field:NotNull
    val uuid: String = "",

    @field:NotNull
    val path: String = "",

    @field:NotNull
    val name: String = "",

    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "post_id")
    val post: Post,

) : BaseTimeEntity()
