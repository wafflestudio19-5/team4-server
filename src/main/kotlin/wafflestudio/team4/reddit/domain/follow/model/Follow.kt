package wafflestudio.team4.reddit.domain.follow.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import wafflestudio.team4.reddit.domain.user.model.User
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_follow")
class Follow(
    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "from_user_id")
    val fromUser: User,

    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "to_user_id")
    val toUser: User,

    @field:NotNull
    var deleted: Boolean = false
) : BaseTimeEntity()
