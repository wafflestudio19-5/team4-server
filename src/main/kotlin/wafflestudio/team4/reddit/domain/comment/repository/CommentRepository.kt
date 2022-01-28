package wafflestudio.team4.reddit.domain.comment.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.comment.model.Comment
import wafflestudio.team4.reddit.domain.post.model.Post

interface CommentRepository : JpaRepository<Comment, Long?> {
    fun findByPostIsAndIdLessThanAndDeletedIsNotAndDepthIsOrderByIdDesc(
        post: Post,
        lastCommentId: Long,
        deleteStatusFilter: Int = 2,
        depth: Int = 0,
        pageable: Pageable
    ): Page<Comment>
    fun findByParentCommentIsAndDeletedIsNot(comment: Comment, deleteStatusFilter: Int = 2): List<Comment>
    fun existsByParentCommentIsAndDeletedIsNot(comment: Comment, deleteStatusFilter: Int = 2): Boolean
}
