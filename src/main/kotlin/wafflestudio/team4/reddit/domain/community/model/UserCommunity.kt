package wafflestudio.team4.reddit.domain.community.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import wafflestudio.team4.reddit.domain.user.model.User
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.ManyToOne
import javax.persistence.FetchType
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_user_community")
class UserCommunity(
    // PK user_community_id Int NOT NULL

    // FK user User NOT NULL
    @field:NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,

    // FK community Community NOT NULL
    @field:NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    val community: Community,

    // manager or user
    @field:NotNull
    var isManager: Boolean = false,

    // joined Boolean NOT NULL
    @field:NotNull
    var joined: Boolean = true

) : BaseTimeEntity()
