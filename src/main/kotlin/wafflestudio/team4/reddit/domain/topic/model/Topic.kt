package wafflestudio.team4.reddit.domain.topic.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_topic")
class Topic(
    // PK topic_id Int NOT NULL
    // name String NOT NULL
    @Column(unique = true)
    @field:NotNull
    val name: String
) : BaseTimeEntity()
