package wafflestudio.team4.reddit.domain.user.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import wafflestudio.team4.reddit.domain.user.dto.UserDto
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
    var email: String,

    @field:NotBlank
    var username: String,

    @field:NotBlank
    var password: String,

    @field:NotNull
    var online: Boolean = false,

    @field:NotNull
    var isDeleted: Boolean = false,

    @field:NotNull
    var roles: String = "",

    // TODO cascade
) : BaseTimeEntity() {
    fun updatedBy(updateRequest: UserDto.UpdateRequest, encodedPassword: String?): User {
        this.email = updateRequest.email ?: this.email
        this.username = updateRequest.username ?: this.username
        this.password = encodedPassword ?: this.password
        return this
    }
}
