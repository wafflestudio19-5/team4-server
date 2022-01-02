package wafflestudio.team4.reddit.domain.comment.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import wafflestudio.team4.reddit.domain.post.model.Post
import wafflestudio.team4.reddit.domain.user.model.User
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.OneToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_comment")
class Comment(

    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "post_id")
    val post: Post,

    @field:NotNull
    val text: String,

    @field:NotNull
    val depth: Int,

    @OneToOne
    @JoinColumn(name = "comment_id")
    val parent: Comment,

    @OneToMany(mappedBy = "comment")
    var votes: MutableList<CommentVote> = mutableListOf(),

    @NotNull
    var deleted: Boolean = false

) : BaseTimeEntity()
