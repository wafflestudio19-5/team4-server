package wafflestudio.team4.reddit.domain.post.model

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

//    @field:NotNull
//    @ManyToOne
//    @JoinColumn(name="community_id")
//    val community: Community, // community 도메인 import 필요

    @field:NotBlank
    var title: String,

    @field:NotBlank
    var content: String,

    @OneToMany(mappedBy = "post")
    var images : MutableList<PostImage> = mutableListOf(),

//    @OneToMany(mappedBy = "post")
//    val videos : List<PostVideo> = listOf(),

    @field:NotNull
    var isDeleted: Boolean = false,

    @OneToMany(mappedBy = "post")
    val votes : List<PostVote> = listOf(),

) : BaseTimeEntity()
