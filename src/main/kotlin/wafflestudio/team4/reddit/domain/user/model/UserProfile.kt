package wafflestudio.team4.reddit.domain.user.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_user_profile")
class UserProfile(
    @field:NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(unique = true)
    val oauthId: String?,

    @OneToOne(mappedBy = "userProfile", cascade = [CascadeType.ALL])
    var userImage: UserImage? = null,

    @field:NotNull
    var description: String = "",
) : BaseTimeEntity()
