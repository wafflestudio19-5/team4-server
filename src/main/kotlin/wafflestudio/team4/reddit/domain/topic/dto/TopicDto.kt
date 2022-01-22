package wafflestudio.team4.reddit.domain.topic.dto

import wafflestudio.team4.reddit.domain.topic.model.Topic
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class TopicDto {
    data class Response(
        val id: Long,

        @field:NotBlank
        val name: String,

        @field:NotNull
        val deleted: Boolean
    ) {
        constructor(topic: Topic) : this(
            id = topic.id,
            name = topic.name,
            deleted = topic.deleted
        )
    }

    data class CreateRequest(
        @field: NotBlank
        val name: String
    )

    data class ModifyRequest(
        @field: NotBlank
        val name: String
    )
}
