package wafflestudio.team4.reddit.domain.user.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_user_image")
class UserImage(
    @field:NotNull
    @OneToOne
    @JoinColumn(name = "user_profile_id")
    val userProfile: UserProfile,

    @field:NotNull
    val url: String = "",
) : BaseTimeEntity()
