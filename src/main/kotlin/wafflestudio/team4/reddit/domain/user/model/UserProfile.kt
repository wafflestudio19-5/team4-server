package wafflestudio.team4.reddit.domain.user.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.OneToOne
import javax.persistence.JoinColumn
import javax.persistence.Column
import javax.persistence.CascadeType
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_user_profile")
class UserProfile(
    @field:NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(unique = true)
    val oauthId: String? = null,

    @OneToOne(mappedBy = "userProfile", cascade = [CascadeType.ALL])
    var userImage: UserImage? = null,

    @field:NotNull
    var description: String = "",

    @field:NotNull
    var name: String = user.username, // nickname
) : BaseTimeEntity()
