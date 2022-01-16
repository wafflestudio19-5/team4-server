package wafflestudio.team4.reddit.domain.topic.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wafflestudio.team4.reddit.domain.topic.dto.TopicDto
import wafflestudio.team4.reddit.domain.topic.exceptions.TopicAlreadyExistsException
import wafflestudio.team4.reddit.domain.topic.exceptions.TopicNotFoundException
import wafflestudio.team4.reddit.domain.topic.model.Topic
import wafflestudio.team4.reddit.domain.topic.repository.TopicRepository
import wafflestudio.team4.reddit.domain.user.model.User

@Service
class TopicService(
    private val topicRepository: TopicRepository
) {
    fun getAllTopics(): List<Topic> {
        return topicRepository.findAll()
    }

    fun getTopicById(id: Long): Topic {
        val topic = topicRepository.findByIdOrNull(id) ?: throw TopicNotFoundException()
        return topic
    }

    fun getTopicByName(name: String): Topic {
        return topicRepository.getByName(name)
    }

    @Transactional
    fun createTopic(createRequest: TopicDto.CreateRequest, user: User): Topic {
        if (topicRepository.existsByName(createRequest.name)) throw TopicAlreadyExistsException()
        // TODO topic is deleted
        var topic = Topic(createRequest.name)
        topic = topicRepository.save(topic)
        return topic
    }

    fun modifyTopic(topicId: Long, modifyRequest: TopicDto.ModifyRequest): Topic {
        val topic = topicRepository.findByIdOrNull(topicId) ?: throw TopicNotFoundException()
        // TODO topic is deleted
        topic.name = modifyRequest.name
        topicRepository.save(topic)
        return topic
    }

    fun deleteTopic(topicId: Long): Topic {
        val topic = topicRepository.findByIdOrNull(topicId) ?: throw TopicNotFoundException()
        // TODO topic is deleted
        topic.deleted = true
        topicRepository.save(topic)
        return topic
    }

    fun checkTopicExistence(name: String): Boolean {
        return topicRepository.existsByName(name)
    }
}
