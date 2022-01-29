package wafflestudio.team4.reddit.domain.community.dto

import com.fasterxml.jackson.annotation.JsonProperty
import wafflestudio.team4.reddit.domain.community.model.Community
import wafflestudio.team4.reddit.domain.topic.dto.TopicDto
import wafflestudio.team4.reddit.domain.user.dto.UserDto
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class CommunityDto {
    data class Response(
        val id: Long,

        @field:NotBlank
        val name: String,

        @JsonProperty("num_members")
        @field:NotNull
        val numMembers: Int,

        @JsonProperty("num_managers")
        @field:NotNull
        val numManagers: Int,

        @field:NotNull
        val managers: List<UserDto.UsernameResponse>,

        // @field:NotBlank
        val description: String,

        @field:NotNull
        val topics: List<TopicDto.Response>,

        @field:NotNull
        val deleted: Boolean
    ) {
        constructor(community: Community) : this(
            id = community.id,
            name = community.name,
            numMembers = community.users.filter { it.joined }.filter { !it.isManager }.size,
            numManagers = community.users.filter { it.joined }.filter { it.isManager }.size,
            managers = community.users.filter { it.isManager }.map { UserDto.UsernameResponse(it.user) },
            description = community.description,
            topics = community.topics.filter { !it.deleted }.map { TopicDto.Response(it.topic) },
            deleted = community.deleted
        )
    }

    // CreateRequest
    data class CreateRequest(
        @field:NotBlank
        val name: String,
        @field:NotBlank
        val description: String,
        @field:NotNull
        val topics: List<String>
    )

    data class ModifyRequest(
        val description: String?,
        val managers: List<String>?,
        val topics: List<String>?
    )

    // ModifyDescriptionRequest
    data class ModifyDescriptionRequest(
        val description: String
    )

    data class Description(
        val description: String
    )
}
