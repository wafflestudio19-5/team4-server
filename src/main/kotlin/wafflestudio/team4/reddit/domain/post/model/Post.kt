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
    val title: String,

    @field:NotBlank
    val content: String,

    @OneToMany(mappedBy = "post")
    val images : List<PostImage> = listOf(),

//    @OneToMany(mappedBy = "post")
//    val videos : List<PostVideo> = listOf(),

    @field:NotNull
    val isDeleted: Boolean = false,

//    file? 은 필요가 있을지...
//    numVote - 리스폰스로 보낼 시 Dto에서 처리
//    numDownVote - 리스폰스로 보낼 시 Dto에서 처리

) : BaseTimeEntity()
