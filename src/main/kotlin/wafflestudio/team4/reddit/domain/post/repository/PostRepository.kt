package wafflestudio.team4.reddit.domain.post.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.post.model.Post

interface PostRepository : JpaRepository<Post, Long?> {
    fun findByIdLessThanOrderByIdDesc(lastPostId: Long  , pageable: Pageable): Page<Post>

}
