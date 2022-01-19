package wafflestudio.team4.reddit.domain.user.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.user.model.User

interface UserRepository : JpaRepository<User, Long?> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun findByEmailAndPassword(email: String, password: String): User?
    fun findByIdLessThanOrderByIdDesc(id: Long, pageable: Pageable): Page<User>
    fun findByIdLessThanAndUsernameLikeOrderByIdDesc(id: Long, pattern: String, pageable: Pageable): Page<User>
}
