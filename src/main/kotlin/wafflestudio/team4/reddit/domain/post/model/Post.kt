package wafflestudio.team4.reddit.domain.post.model

import wafflestudio.team4.reddit.domain.community.model.Community
import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import wafflestudio.team4.reddit.domain.user.model.User
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_post")
class Post(

    @field:NotNull
    @ManyToOne
    @JoinColumn(name="user_id")
    val user: User,

    @field:NotNull
    @ManyToOne
    @JoinColumn(name="community_id")
    val community: Community,

    @field:NotBlank
    var title: String,

    var text: String? = "",

    @OneToMany(mappedBy = "post")
    var images : MutableList<PostImage>? = mutableListOf(),

//    @OneToMany(mappedBy = "post")
//    val videos : List<PostVideo> = listOf(),

    @field:NotNull
    var deleted: Boolean = false,

    @OneToMany(mappedBy = "post")
    var votes : MutableList<PostVote> = mutableListOf(),

    ) : BaseTimeEntity()
