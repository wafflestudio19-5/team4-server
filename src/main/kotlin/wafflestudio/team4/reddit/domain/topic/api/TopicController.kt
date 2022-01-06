package wafflestudio.team4.reddit.domain.topic.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.DeleteMapping
import wafflestudio.team4.reddit.domain.topic.dto.TopicDto
import wafflestudio.team4.reddit.domain.topic.service.TopicService
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.global.auth.annotation.CurrentUser
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/topics")
class TopicController(
    private val topicService: TopicService
) {
    @GetMapping("/")
    fun getTopics() {
    }

    @GetMapping("/{topic_id}/")
    fun getTopicById() {
    }

    // anyone can create topic
    @PostMapping("/")
    fun createTopic(@CurrentUser user: User, @Valid @RequestBody createRequest: TopicDto.CreateRequest):
        ResponseEntity<TopicDto.Response> {
        val topic = topicService.createTopic(createRequest, user)
        return ResponseEntity.status(201).body(TopicDto.Response(topic))
    }

    @DeleteMapping("/{topic_id}/")
    fun deleteTopic() {
    }
}
