package wafflestudio.team4.reddit.domain.topic.api

import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import wafflestudio.team4.reddit.domain.topic.dto.TopicDto
import wafflestudio.team4.reddit.domain.topic.service.TopicService
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.global.common.dto.ListResponse
import wafflestudio.team4.reddit.global.auth.annotation.CurrentUser
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/topics")
class TopicController(
    private val topicService: TopicService
) {
    @GetMapping("/")
    fun getTopics(): ListResponse<TopicDto.Response> { // did not apply pagination
        val topics = topicService.getAllTopics()
        return ListResponse(topics.map { TopicDto.Response(it) })
    }

    @GetMapping("/{topic_id}/")
    fun getTopicById(@PathVariable("topic_id") topicId: Long): ResponseEntity<TopicDto.Response> {
        val topic = topicService.getTopicById(topicId)
        return ResponseEntity.status(200).body(TopicDto.Response(topic))
    }

    // TODO only admin creates topic
    @PostMapping("/")
    @Transactional
    fun createTopic(@CurrentUser user: User, @Valid @RequestBody createRequest: TopicDto.CreateRequest):
        ResponseEntity<TopicDto.Response> {
        val topic = topicService.createTopic(createRequest, user)
        return ResponseEntity.status(201).body(TopicDto.Response(topic))
    }

    @PutMapping("/{topic_id}/")
    @Transactional
    fun modifyTopic(@PathVariable("topic_id") topicId: Long, @Valid @RequestBody modifyRequest: TopicDto.ModifyRequest):
        ResponseEntity<TopicDto.Response> {
        // change topic name
        val topic = topicService.modifyTopic(topicId, modifyRequest)
        return ResponseEntity.status(200).body(TopicDto.Response(topic))
    }

    @DeleteMapping("/{topic_id}/")
    @Transactional
    fun deleteTopic(@PathVariable("topic_id") topicId: Long): ResponseEntity<TopicDto.Response> {
        val topic = topicService.deleteTopic(topicId)
        return ResponseEntity.status(200).body(TopicDto.Response(topic))
    }
}
