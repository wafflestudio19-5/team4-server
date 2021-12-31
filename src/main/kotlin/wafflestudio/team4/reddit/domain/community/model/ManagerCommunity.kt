package wafflestudio.team4.reddit.domain.community.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import wafflestudio.team4.reddit.domain.user.model.User
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_manager_community")
class ManagerCommunity (
    @field:NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    val manager: User,

    @field:NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    val community: Community,

    @field:NotNull
    var joined: Boolean = true
): BaseTimeEntity()

