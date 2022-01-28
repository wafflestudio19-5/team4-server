package wafflestudio.team4.reddit.domain.post.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.post.model.Post

interface PostRepository : JpaRepository<Post, Long?> {
    fun findByIdLessThanAndDeletedIsFalseOrderByIdDesc(lastPostId: Long, pageable: Pageable): Page<Post>
    fun findByCommunityIdEqualsAndIdLessThanAndDeletedIsFalseOrderByIdDesc(
        communityId: Long,
        lastPostId: Long,
        pageRequest: Pageable
    ): Page<Post>
    fun findByIdLessThanAndDeletedIsFalseAndTitleLikeOrderByIdDesc(
        lastPostId: Long,
        pattern: String,
        pageable: Pageable
    ): Page<Post>
}
