package wafflestudio.team4.reddit.domain.community.repository

import wafflestudio.team4.reddit.domain.community.model.UserCommunity
import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.community.model.Community
import wafflestudio.team4.reddit.domain.user.model.User

interface UserCommunityRepository : JpaRepository<UserCommunity, Long?> {
    fun getByUserAndCommunity(user: User, community: Community): UserCommunity
}
