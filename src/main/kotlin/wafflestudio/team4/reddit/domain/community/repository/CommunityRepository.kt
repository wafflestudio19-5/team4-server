package wafflestudio.team4.reddit.domain.community.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import wafflestudio.team4.reddit.domain.community.model.Community
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityRepository : JpaRepository<Community, Long?> {
    fun existsByName(name: String): Boolean
    fun getByName(name: String): Community
    fun findByIdLessThanOrderByIdDesc(lastCommunityId: Long, pageRequest: Pageable): Page<Community>
    fun findByName(name: String): Community?
}
