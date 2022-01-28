package wafflestudio.team4.reddit.domain.community.dto

import com.fasterxml.jackson.annotation.JsonProperty
import wafflestudio.team4.reddit.domain.community.model.Community
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
        // @field:NotBlank
        val description: String,
        @field:NotNull
        val deleted: Boolean
    ) {
        constructor(community: Community) : this(
            id = community.id,
            name = community.name,
            numMembers = community.num_members,
            numManagers = community.num_managers,
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
        val topics: List<String>
    )

    data class ModifyRequest(
        val description: String?,
        val managers: List<String>?,
        val topics: List<String>?
    )

    // ModifyDescriptionRequest
    data class ModifyDescriptionRequest(
        // val name: String,
        val description: String,
        // val topics: List<String>,
    )

    data class Description(
        val description: String
    )
}
