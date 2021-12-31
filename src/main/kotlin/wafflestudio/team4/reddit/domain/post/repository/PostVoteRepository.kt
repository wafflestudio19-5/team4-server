package wafflestudio.team4.reddit.domain.post.repository

import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.post.model.Post
import wafflestudio.team4.reddit.domain.post.model.PostVote
import wafflestudio.team4.reddit.domain.user.model.User

interface PostVoteRepository : JpaRepository<PostVote, Long?> {
    fun existsByPostAndUser(post: Post, user: User):Boolean
    fun findByPostAndUser(post: Post, user: User):PostVote
}
