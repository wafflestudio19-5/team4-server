package wafflestudio.team4.reddit.domain.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.user.model.UserImage

interface UserImageRepository : JpaRepository<UserImage, Long?>
