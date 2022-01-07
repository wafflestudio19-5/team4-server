package wafflestudio.team4.reddit.domain.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.user.model.UserImage
import wafflestudio.team4.reddit.domain.user.model.UserProfile

interface UserImageRepository : JpaRepository<UserImage, Long?> {
    fun findByUserProfile(profile: UserProfile): UserImage?
}
