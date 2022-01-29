package wafflestudio.team4.reddit.domain.community.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Column
import javax.persistence.OneToMany
import javax.persistence.CascadeType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_community")
class Community(
    @Column(unique = true)
    @field:NotBlank
    var name: String,

    // @field:NotNull
    // var num_members: Int = 0,

    // @field:NotNull
    // var num_managers: Int = 0,

    @OneToMany(mappedBy = "community", cascade = [CascadeType.ALL]) // why merge?
    var users: MutableSet<UserCommunity> = mutableSetOf(),

    // @field:NotBlank
    var description: String = "",

    @OneToMany(mappedBy = "community", cascade = [CascadeType.ALL]) // why merge?
    var topics: MutableSet<CommunityTopic> = mutableSetOf(),

    @field:NotNull
    var deleted: Boolean = false
) : BaseTimeEntity()
