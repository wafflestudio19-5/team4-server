package wafflestudio.team4.reddit.domain.community.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import wafflestudio.team4.reddit.domain.community.model.Community
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityRepository : JpaRepository<Community, Long?> {
    fun existsByName(name: String): Boolean
    fun getByName(name: String): Community
    fun findByIdLessThanAndDeletedFalseOrderByIdDesc(lastCommunityId: Long, pageRequest: Pageable): Page<Community>
    fun findByIdInAndIdLessThanAndDeletedFalseOrderByIdDesc(
        communityIds: List<Long>,
        lastCommunityId: Long,
        pageRequest: Pageable
    ): Page<Community>
    fun findByName(name: String): Community?
    fun findByNameLikeAndDeletedFalse(keywordPattern: String): List<Community>
    fun findByIdLessThanAndNameLikeAndDeletedFalseOrderByIdDesc(
        lastCommunityId: Long,
        keywordPattern: String,
        pageRequest: Pageable
    ): Page<Community>
}
