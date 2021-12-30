package wafflestudio.team4.reddit.domain.community.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import wafflestudio.team4.reddit.domain.community.model.Community
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityRepository : JpaRepository<Community, Long?> {
    override fun getById(id: Long): Community
    override fun findAll(): List<Community>
    fun existsByName(name: String): Boolean
    fun findByIdLessThanOrderByIdDesc(id: Long, pageable: Pageable): Page<Community>
}
