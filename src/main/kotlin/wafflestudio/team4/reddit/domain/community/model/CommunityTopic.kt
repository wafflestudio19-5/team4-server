package wafflestudio.team4.reddit.domain.community.model

import wafflestudio.team4.reddit.domain.model.BaseTimeEntity
import wafflestudio.team4.reddit.domain.topic.model.Topic
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.ManyToOne
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.validation.constraints.NotNull

@Entity
@Table(name = "reddit_community_topic")
class CommunityTopic(
    @field:NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    val community: Community,

    @field:NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    val topic: Topic,

    @field:NotNull
    var deleted: Boolean = false
) : BaseTimeEntity()
