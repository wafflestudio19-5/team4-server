package wafflestudio.team4.reddit.domain.user.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_user")
class User(
    @Column(unique = true)
    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val username: String,

    @field:NotBlank
    val password: String,

    @field:NotNull
    val online: Boolean = false,

    @field:NotNull
    val isDeleted: Boolean = false,

    @field:NotNull
    val roles: String = "",

    // TODO cascade
) : BaseTimeEntity()
