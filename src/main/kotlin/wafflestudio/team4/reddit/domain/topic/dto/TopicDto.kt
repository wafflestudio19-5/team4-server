package wafflestudio.team4.reddit.domain.topic.dto

import wafflestudio.team4.reddit.domain.topic.model.Topic
import javax.validation.constraints.NotBlank

class TopicDto {
    data class Response(
        val id: Long,
        val name: String
    ) {
        constructor(topic: Topic) : this(
            id = topic.id,
            name = topic.name
        )
    }

    data class CreateRequest(
        @field: NotBlank
        val name: String
    )
}
