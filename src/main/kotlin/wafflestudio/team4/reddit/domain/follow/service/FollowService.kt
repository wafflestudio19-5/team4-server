package wafflestudio.team4.reddit.domain.follow.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wafflestudio.team4.reddit.domain.follow.exception.AlreadyFollowingException
import wafflestudio.team4.reddit.domain.follow.exception.NotFollowingException
import wafflestudio.team4.reddit.domain.follow.exception.SelfFollowException
import wafflestudio.team4.reddit.domain.follow.model.Follow
import wafflestudio.team4.reddit.domain.follow.repository.FollowRepository
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.service.UserService

@Service
class FollowService(
    private val followRepository: FollowRepository,
    private val userService: UserService
) {
    fun getFollowersPage(toUserId: Long, lastFollowId: Long, size: Int): Page<Follow> {
        val pageRequest = Pageable.ofSize(size)
        // equals toUserId, followId less than, pageRequest
        return followRepository.findByToUserIdEqualsAndIdLessThanOrderByIdDesc(toUserId, lastFollowId, pageRequest)
    }

    @Transactional
    fun follow(fromUser: User, toUserId: Long): Follow {
        if (fromUser.id == toUserId) throw SelfFollowException()
        val toUser = userService.getUserById(toUserId)
        if (!followRepository.existsByFromUserAndToUser(fromUser, toUser)) {
            var follow = Follow(fromUser, toUser)
            follow = followRepository.save(follow)
            return follow
        }
        var follow = followRepository.getByFromUserAndToUser(fromUser, toUser)
        // check if already following
        if (!follow.deleted) throw AlreadyFollowingException()
        follow.deleted = false
        follow = followRepository.save(follow)
        return follow
    }

    @Transactional
    fun unfollow(fromUser: User, toUserId: Long): Follow {
        val toUser = userService.getUserById(toUserId)
        // check if already following
        if (!followRepository.existsByFromUserAndToUser(fromUser, toUser)) throw NotFollowingException()
        var follow = followRepository.getByFromUserAndToUser(fromUser, toUser)
        if (follow.deleted) throw NotFollowingException()
        follow.deleted = true
        follow = followRepository.save(follow)
        return follow
    }
}
