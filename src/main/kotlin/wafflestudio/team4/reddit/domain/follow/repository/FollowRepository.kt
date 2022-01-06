package wafflestudio.team4.reddit.domain.follow.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.follow.model.Follow
import wafflestudio.team4.reddit.domain.user.model.User

interface FollowRepository : JpaRepository<Follow, Long?> {
    fun getByFromUserAndToUser(fromUser: User, toUser: User): Follow
    fun existsByFromUserAndToUser(fromUser: User, toUser: User): Boolean
    fun findByFromUserIdLessThanOrderByIdDesc(
        fromUserId: Long,
        lastFollowId: Long,
        pageRequest: PageRequest
    ): Page<Follow>
}
