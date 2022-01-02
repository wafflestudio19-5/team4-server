package wafflestudio.team4.reddit.domain.comment.repository

import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.comment.model.CommentVote

interface CommentVoteRepository : JpaRepository<CommentVote, Long?>
