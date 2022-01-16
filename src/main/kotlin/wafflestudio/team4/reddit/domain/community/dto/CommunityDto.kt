package wafflestudio.team4.reddit.domain.community.dto

import wafflestudio.team4.reddit.domain.community.model.Community
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class CommunityDto {
    data class Response(
        val id: Long,
        @field:NotBlank
        val name: String,
        @field:NotNull
        val num_members: Int,
        @field:NotNull
        val num_managers: Int,
        // @field:NotBlank
        val description: String,
        @field:NotNull
        val deleted: Boolean
    ) {
        constructor(community: Community) : this(
            id = community.id,
            name = community.name,
            num_members = community.num_members,
            num_managers = community.num_managers,
            description = community.description,
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
        val topics: List<String> // at least one topic
    )

    // ModifyRequest
    data class ModifyDescriptionRequest(
        // val name: String,
        val description: String,
        // val topics: List<String>,
    )

    data class Description(
        val description: String
    )
}
