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
    var text: String,

    @field:NotNull
    val depth: Int,

    @OneToOne
    @JoinColumn(name = "comment_id")
    val parent: Comment,

    @OneToMany(mappedBy = "comment")
    var votes: MutableList<CommentVote> = mutableListOf(),

    @NotNull
    // 0: exist 1: deleted, but has children comments (will not be filtered and be included in response)
    // 2: deleted (will be filtered with jpa query)
    // 항상 같은 수의 댓글을 넘겨주기 위해 이렇게 설정했습니다...
    var deleted: Int = 0

) : BaseTimeEntity()
