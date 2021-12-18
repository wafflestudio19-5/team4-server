package wafflestudio.team4.reddit.domain.community.repository

import wafflestudio.team4.reddit.domain.community.model.CommunityTopic
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityTopicRepository : JpaRepository<CommunityTopic, Long?>
