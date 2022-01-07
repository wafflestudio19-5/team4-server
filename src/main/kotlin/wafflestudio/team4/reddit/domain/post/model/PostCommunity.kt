package wafflestudio.team4.reddit.domain.post.model

import wafflestudio.team4.reddit.domain.community.model.Community
import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_post_community")
class PostCommunity(
    @field:NotNull
    @ManyToOne // TODO onetoone?
    @JoinColumn(name = "post_id")
    val post: Post,

    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "community_id")
    val community: Community,

    @field:NotNull
    val isPinned: Boolean = false

) : BaseTimeEntity()
