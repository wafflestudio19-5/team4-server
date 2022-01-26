package wafflestudio.team4.reddit.domain.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseTimeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0,

    @CreatedDate
    open var createdAt: LocalDateTime? = LocalDateTime.now(),

    @LastModifiedDate
    open var updatedAt: LocalDateTime? = LocalDateTime.now()
) : Comparable<Any> {
    override fun compareTo(other: Any): Int {
        return 0
    }
}
