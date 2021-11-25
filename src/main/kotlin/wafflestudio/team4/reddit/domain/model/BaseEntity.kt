package wafflestudio.team4.reddit.domain.model

import javax.persistence.*

@MappedSuperclass
open class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0
}