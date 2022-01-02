package wafflestudio.team4.reddit.domain.comment.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.comment.model.Comment

interface CommentRepository : JpaRepository<Comment, Long?> {
    fun findByIdLessThanAndDeletedIsFalseOrderByIdDesc(lastPostId: Long, pageable: Pageable): Page<Comment>
}
