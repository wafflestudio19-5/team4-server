package wafflestudio.team4.reddit.domain.comment.repository

import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.comment.model.Comment
import wafflestudio.team4.reddit.domain.comment.model.CommentVote
import wafflestudio.team4.reddit.domain.user.model.User

interface CommentVoteRepository : JpaRepository<CommentVote, Long?> {
    fun existsByCommentAndUser(comment: Comment, user: User): Boolean
    fun findByCommentAndUser(comment: Comment, user: User): CommentVote
}
