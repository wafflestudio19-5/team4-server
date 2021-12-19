package wafflestudio.team4.reddit.domain.post.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "reddit_tag")
class Tag(
    @field:NotBlank
    val name: String,

) : BaseTimeEntity()
