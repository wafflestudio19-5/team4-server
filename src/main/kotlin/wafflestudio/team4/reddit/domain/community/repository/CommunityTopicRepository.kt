package wafflestudio.team4.reddit.domain.community.repository

import wafflestudio.team4.reddit.domain.community.model.CommunityTopic
import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.community.model.Community
import wafflestudio.team4.reddit.domain.topic.model.Topic

interface CommunityTopicRepository : JpaRepository<CommunityTopic, Long?> {
    fun getByCommunityAndTopic(community: Community, topic: Topic): CommunityTopic
    fun existsByCommunityAndTopic(community: Community, topic: Topic): Boolean
    fun getAllByCommunity(community: Community): List<CommunityTopic>
    fun existsByCommunity(community: Community): Boolean
}
