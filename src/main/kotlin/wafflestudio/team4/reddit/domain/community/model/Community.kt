package wafflestudio.team4.reddit.domain.community.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_community")
class Community(

    @Column(unique = true)
    @field:NotBlank
    var name: String,

    /*@OneToMany(cascade = [CascadeType.MERGE], mappedBy = "charge_community", fetch = FetchType.LAZY)
    var managers: MutableSet<User> = mutableSetOf(),
    */

    @field:NotNull
    var num_members: Int = 0,

    @field:NotNull
    var num_managers: Int = 0,

    @field:NotBlank
    var description: String,

    @field:NotNull
    var deleted: Boolean = false
) : BaseTimeEntity()
