package wafflestudio.team4.reddit.domain.community.repository

import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.community.model.Community
import wafflestudio.team4.reddit.domain.community.model.ManagerCommunity
import wafflestudio.team4.reddit.domain.user.model.User

interface ManagerCommunityRepository : JpaRepository<ManagerCommunity, Long?> {
    fun getByManagerAndCommunity(manager: User, community: Community): ManagerCommunity
    fun existsByManagerAndCommunity(manager: User, community: Community): Boolean
}
