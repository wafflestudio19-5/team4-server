package wafflestudio.team4.reddit.domain.topic.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import wafflestudio.team4.reddit.domain.topic.dto.TopicDto
import wafflestudio.team4.reddit.domain.topic.exceptions.TopicAlreadyExistsException
import wafflestudio.team4.reddit.domain.topic.exceptions.TopicDeletedException
import wafflestudio.team4.reddit.domain.topic.exceptions.TopicNotFoundException
import wafflestudio.team4.reddit.domain.topic.model.Topic
import wafflestudio.team4.reddit.domain.topic.repository.TopicRepository
import wafflestudio.team4.reddit.domain.user.model.User
import java.time.LocalDateTime

@Service
class TopicService(
    private val topicRepository: TopicRepository
) {
    fun getAllTopics(): List<Topic> {
        return topicRepository.findByDeletedFalse()
    }

    fun getTopicById(id: Long): Topic {
        val topic = topicRepository.findByIdOrNull(id) ?: throw TopicNotFoundException()
        if (topic.deleted) throw TopicDeletedException()
        return topic
    }

    fun getTopicByName(name: String): Topic {
        if (!topicRepository.existsByName(name)) throw TopicNotFoundException()
        val topic = topicRepository.getByName(name)
        if (topic.deleted) throw TopicDeletedException()
        return topic
    }

    fun createTopic(createRequest: TopicDto.CreateRequest, user: User): Topic {
        if (topicRepository.existsByName(createRequest.name)) {
            var oldTopic = topicRepository.getByName(createRequest.name)
            if (!oldTopic.deleted) throw TopicAlreadyExistsException()
            oldTopic.name += ("-deprecated" + LocalDateTime.now() + user.id)
            topicRepository.save(oldTopic)
        }

        var topic = Topic(createRequest.name)
        topic = topicRepository.save(topic)
        return topic
    }

    fun modifyTopic(topicId: Long, modifyRequest: TopicDto.ModifyRequest): Topic {
        val topic = topicRepository.findByIdOrNull(topicId) ?: throw TopicNotFoundException()
        if (topic.deleted) throw TopicDeletedException()
        topic.name = modifyRequest.name
        topicRepository.save(topic)
        return topic
    }

    fun deleteTopic(topicId: Long): Topic {
        val topic = topicRepository.findByIdOrNull(topicId) ?: throw TopicNotFoundException()
        if (topic.deleted) throw TopicDeletedException()
        topic.deleted = true
        topicRepository.save(topic)
        return topic
    }

    fun checkTopicExistence(name: String): Boolean {
        if (!topicRepository.existsByName(name)) return false
        val topic = topicRepository.getByName(name)
        return !topic.deleted
    }
}
