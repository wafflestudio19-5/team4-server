package wafflestudio.team4.reddit.domain.topic.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wafflestudio.team4.reddit.domain.topic.dto.TopicDto
import wafflestudio.team4.reddit.domain.topic.model.Topic
import wafflestudio.team4.reddit.domain.topic.repository.TopicRepository
import wafflestudio.team4.reddit.domain.user.model.User

@Service
class TopicService(
    private val topicRepository: TopicRepository
) {
    fun getTopicById(id: Long): Topic {
        return topicRepository.getById(id)
    }

    fun getTopicByName(name: String): Topic {
        return topicRepository.getByName(name)
    }

    @Transactional
    fun createTopic(createRequest: TopicDto.CreateRequest, user: User): Topic {
        var topic = Topic(createRequest.name)
        topic = topicRepository.save(topic)
        return topic
    }

    fun checkTopicExistence(name: String): Boolean {
        return topicRepository.existsByName(name)
    }

}
