package wafflestudio.team4.reddit.domain.follow.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.follow.model.Follow
import wafflestudio.team4.reddit.domain.user.model.User

interface FollowRepository : JpaRepository<Follow, Long?> {
    fun getByFromUserAndToUser(fromUser: User, toUser: User): Follow
    fun existsByFromUserAndToUser(fromUser: User, toUser: User): Boolean
    fun findByToUserIdEqualsAndIdLessThanOrderByIdDesc(
        toUserId: Long,
        lastFollowId: Long,
        pageRequest: Pageable
    ): Page<Follow>
    fun findByToUser(toUser: User): List<Follow>
}
