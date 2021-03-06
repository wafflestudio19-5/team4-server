package wafflestudio.team4.reddit.domain.comment.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import wafflestudio.team4.reddit.domain.user.model.User
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_comment_vote")
class CommentVote(

    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "comment_id")
    val comment: Comment,

    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    // 2 - up vote, 0  - down vote, 1 - cancelled vote
    @field:NotNull
    var isUp: Int = 0,

) : BaseTimeEntity()
