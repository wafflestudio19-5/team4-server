package wafflestudio.team4.reddit.domain.model

import javax.persistence.MappedSuperclass
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@MappedSuperclass
open class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0
}
