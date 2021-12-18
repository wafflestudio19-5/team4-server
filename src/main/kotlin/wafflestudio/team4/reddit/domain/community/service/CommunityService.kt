package wafflestudio.team4.reddit.domain.community.service

import org.springframework.stereotype.Service
import wafflestudio.team4.reddit.domain.community.dto.CommunityDto
import wafflestudio.team4.reddit.domain.community.exception.NotCommunityManagerException
import wafflestudio.team4.reddit.domain.community.model.Community
import wafflestudio.team4.reddit.domain.community.model.CommunityTopic
import wafflestudio.team4.reddit.domain.community.model.UserCommunity
import wafflestudio.team4.reddit.domain.community.repository.CommunityRepository
import wafflestudio.team4.reddit.domain.community.repository.CommunityTopicRepository
import wafflestudio.team4.reddit.domain.community.repository.UserCommunityRepository
import wafflestudio.team4.reddit.domain.topic.model.Topic
import wafflestudio.team4.reddit.domain.user.model.User

@Service
class CommunityService(
    private val communityRepository: CommunityRepository,
    private val userCommunityRepository: UserCommunityRepository,
    private val communityTopicRepository: CommunityTopicRepository
) {
    fun getAllCommunities(): List<Community> {
        return communityRepository.findAll()
    }

    fun getCommunityById(communityId: Long): Community {
        // TODO exception: if community with id x exist
        return communityRepository.getById(communityId)
    }

    fun createCommunity(createRequest: CommunityDto.CreateRequest, user: User): Community {
        var community = Community(
            name = createRequest.name,
            num_members = 0, // managers not part of num_members
            num_managers = 1,
            description = createRequest.description
        )
        community = communityRepository.save(community)

        // creator becomes one of managers
        val userCommunity = UserCommunity(
            user = user,
            community = community,
            isManager = true,
        )
        userCommunityRepository.save(userCommunity)

        val topics = createRequest.topics.map { Topic(name = it) }
        val communityTopic = CommunityTopic(
            community = community,
            topic = topics[0] // at least one topic, first topic
        )
        communityTopicRepository.save(communityTopic)

        // add additional topics
        if (topics.isNotEmpty()) {
            for (i in 1 until topics.size) {
                val newCommunityTopic = CommunityTopic(
                    community = community,
                    topic = topics[i]
                )
                communityTopicRepository.save(newCommunityTopic)
            }
        }

        return community
    }

    fun joinCommunity(user: User, communityId: Long, role: String): Community {
        // TODO exception: if already joined (member or manager)
        // TODO exception: if community with id x exist
        var community = communityRepository.getById(communityId)

        val userCommunity = UserCommunity(
            user = user,
            community = community
        )
        if (role == "manager") {
            userCommunity.isManager = true
            community.num_managers += 1
        } else community.num_members += 1

        userCommunityRepository.save(userCommunity)
        community = communityRepository.save(community)

        return community
    }

    fun leaveCommunity(user: User, communityId: Long): Community {
        // TODO exception: if user not in community (already left or never joined)
        // TODO exception: if community with id x exist
        var community = communityRepository.getById(communityId)

        val userCommunity = userCommunityRepository.getByUserAndCommunity(user, community)
        if (userCommunity.isManager) community.num_managers -= 1
        else community.num_members -= 1
        userCommunity.joined = false

        userCommunityRepository.save(userCommunity)
        community = communityRepository.save(community)

        return community
    }

    fun modifyCommunity(user: User, modifyRequest: CommunityDto.ModifyRequest, communityId: Long): Community {
        // TODO exception: if community with id x exist

        var community = communityRepository.getById(communityId)

        // check if user is this community's manager
        val userCommunity = userCommunityRepository.getByUserAndCommunity(user, community)
        if (!userCommunity.isManager) throw NotCommunityManagerException()

        if (modifyRequest.name != "") community.name = modifyRequest.name
        // null or ""?

        // add manager?
        // TODO add, delete topics

        if (modifyRequest.description != "") community.description = modifyRequest.description

        community = communityRepository.save(community)
        userCommunityRepository.save(userCommunity)

        return community
    }

    fun deleteCommunity(user: User, communityId: Long) {
        // TODO exception: if community with id x exist
        val community = communityRepository.getById(communityId)
        // check whether user is this community's manager
        val userCommunity = userCommunityRepository.getByUserAndCommunity(user, community)
        if (!userCommunity.isManager) throw NotCommunityManagerException()

        community.deleted = true
        communityRepository.save(community)
        // what to return?
        return
    }
}
