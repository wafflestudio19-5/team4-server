package wafflestudio.team4.reddit.domain.post.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import wafflestudio.team4.reddit.domain.user.model.User
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_post_vote")
class PostVote(
    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "post_id")
    val post: Post,

    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "voter_id")
    val voter: User,

    @field:NotNull
    val isUp: Boolean = true,

) : BaseTimeEntity()
