package wafflestudio.team4.reddit.domain.post.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_post_image")
class PostImage(

    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "post_id")
    val post: Post,

    @field:NotNull
    val url: String = "",



) : BaseTimeEntity()
