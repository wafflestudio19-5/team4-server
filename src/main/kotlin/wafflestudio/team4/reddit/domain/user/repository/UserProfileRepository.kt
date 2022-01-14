package wafflestudio.team4.reddit.domain.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.user.model.UserProfile

interface UserProfileRepository : JpaRepository<UserProfile, Long?> {
    fun findByOauthId(OAuthId: String): UserProfile?
}
