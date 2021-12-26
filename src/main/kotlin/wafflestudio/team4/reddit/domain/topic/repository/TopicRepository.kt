package wafflestudio.team4.reddit.domain.topic.repository

import org.springframework.data.jpa.repository.JpaRepository
import wafflestudio.team4.reddit.domain.topic.model.Topic

interface TopicRepository : JpaRepository<Topic, Long?> {
    fun existsByName(name: String): Boolean
    fun getByName(name: String): Topic
}
