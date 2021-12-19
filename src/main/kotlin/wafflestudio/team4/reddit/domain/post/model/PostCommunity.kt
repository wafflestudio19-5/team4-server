package wafflestudio.team4.reddit.domain.post.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_post_community")
class PostCommunity(
    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "post_id")
    val post: Post,

//    @field:NotNull
//    @ManyToOne
//    @JoinColumn(name = "community_id")
//    val community: Community,

    @field:NotNull
    val isPinned: Boolean = false

) : BaseTimeEntity()
