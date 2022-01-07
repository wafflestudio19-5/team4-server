package wafflestudio.team4.reddit.domain.community.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import wafflestudio.team4.reddit.domain.community.dto.CommunityDto
import wafflestudio.team4.reddit.domain.community.exception.CommunityNotFoundException
import wafflestudio.team4.reddit.domain.community.exception.CommunityAlreadyExistsException
import wafflestudio.team4.reddit.domain.community.exception.CommunityDeletedException
import wafflestudio.team4.reddit.domain.community.exception.AlreadyJoinedException
import wafflestudio.team4.reddit.domain.community.exception.NotCurrentlyJoinedException
import wafflestudio.team4.reddit.domain.community.exception.NotCommunityManagerException
import wafflestudio.team4.reddit.domain.community.model.Community
import wafflestudio.team4.reddit.domain.community.model.CommunityTopic
import wafflestudio.team4.reddit.domain.community.model.UserCommunity
import wafflestudio.team4.reddit.domain.community.repository.CommunityRepository
import wafflestudio.team4.reddit.domain.community.repository.CommunityTopicRepository
import wafflestudio.team4.reddit.domain.community.repository.UserCommunityRepository
import wafflestudio.team4.reddit.domain.topic.exceptions.TopicNotFoundException
import wafflestudio.team4.reddit.domain.topic.model.Topic
import wafflestudio.team4.reddit.domain.topic.service.TopicService
import wafflestudio.team4.reddit.domain.user.model.User

@Service
class CommunityService(
    private val communityRepository: CommunityRepository,
    private val userCommunityRepository: UserCommunityRepository,
    private val communityTopicRepository: CommunityTopicRepository,
    private val topicService: TopicService
) {

    // used in search query
    fun getCommunitiesPage(lastCommunityId: Long, size: Int): Page<Community> {
        val pageRequest = Pageable.ofSize(size)
        return communityRepository.findByIdLessThanOrderByIdDesc(lastCommunityId, pageRequest)
    }

    fun getCommunityById(communityId: Long): Community {
        if (!communityRepository.existsById(communityId)) throw CommunityNotFoundException()
        val community = communityRepository.getById(communityId)
        if (community.deleted) throw CommunityDeletedException()
        return community
    }

    fun createCommunity(createRequest: CommunityDto.CreateRequest, user: User): Community {
        if (communityRepository.existsByName(createRequest.name)) throw CommunityAlreadyExistsException()
        // TODO what if community with same name but deleted == true exists?
        var community = Community(
            name = createRequest.name,
            num_members = 0, // managers not part of num_members
            num_managers = 1,
            description = createRequest.description,
            deleted = false
        )
        community = communityRepository.save(community)

        // creator becomes one of managers
        val userCommunity = UserCommunity(
            user = user,
            community = community,
            isManager = true,
        )
        userCommunityRepository.save(userCommunity)

        val topics = mutableListOf<Topic>()
        for (topicName in createRequest.topics) {
            if (!topicService.checkTopicExistence(topicName)) throw TopicNotFoundException()
            val topic = topicService.getTopicByName(topicName)
            topics.add(topic)
        }
        // val topics = createRequest.topics.map { Topic(name = it) }
        val communityTopic = CommunityTopic(
            community = community,
            topic = topics[0] // at least one topic, first topic
        )
        communityTopicRepository.save(communityTopic)

        // add additional topics
        if (topics.size > 1) {
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
        if (!communityRepository.existsById(communityId)) throw CommunityNotFoundException()
        var community = communityRepository.getById(communityId)
        if (community.deleted) throw CommunityDeletedException()

        var userCommunity = UserCommunity(
            user = user,
            community = community
        )
        if (userCommunityRepository.existsByUserAndCommunity(user, community)) {
            userCommunity = userCommunityRepository.getByUserAndCommunity(user, community)
            if (userCommunity.joined) throw AlreadyJoinedException()
            else userCommunity.joined = true
        }

        if (role == "manager") {
            userCommunity.isManager = true
            community.num_managers += 1
        } else if (role == "member") community.num_members += 1

        userCommunityRepository.save(userCommunity)
        community = communityRepository.save(community)

        return community
    }

    fun leaveCommunity(user: User, communityId: Long): Community {
        if (!communityRepository.existsById(communityId)) throw CommunityNotFoundException()
        var community = communityRepository.getById(communityId)
        if (community.deleted) throw CommunityDeletedException()

        if (!userCommunityRepository.existsByUserAndCommunity(user, community)) throw NotCurrentlyJoinedException()
        val userCommunity = userCommunityRepository.getByUserAndCommunity(user, community)
        if (!userCommunity.joined) throw NotCurrentlyJoinedException()

        if (userCommunity.isManager) community.num_managers -= 1
        else community.num_members -= 1
        userCommunity.joined = false

        userCommunityRepository.save(userCommunity)
        community = communityRepository.save(community)

        return community
    }

    fun modifyCommunity(user: User, modifyRequest: CommunityDto.ModifyRequest, communityId: Long): Community {
        if (!communityRepository.existsById(communityId)) throw CommunityNotFoundException()
        var community = communityRepository.getById(communityId)
        if (community.deleted) throw CommunityDeletedException()

        // check if user is this community's manager
        if (!userCommunityRepository.existsByUserAndCommunity(user, community)) throw NotCommunityManagerException()
        val userCommunity = userCommunityRepository.getByUserAndCommunity(user, community)
        if (!userCommunity.joined) throw NotCommunityManagerException()
        if (!userCommunity.isManager) throw NotCommunityManagerException()
        // null or ""?

        // add manager?

        // add, delete topics
        // delete newly deleted topics
        val oldTopics = communityTopicRepository.getAllByCommunity(community)
        for (communityTopic in oldTopics) {
            if (communityTopic.topic.name !in modifyRequest.topics) communityTopic.deleted = true
            communityTopicRepository.save(communityTopic)
        }

        // add newly added topics
        if (!modifyRequest.topics.isEmpty()) {
            for (topicName in modifyRequest.topics) {
                // check if topic is an existing topic
                if (!topicService.checkTopicExistence(topicName)) throw TopicNotFoundException()
                val topic = topicService.getTopicByName(topicName)
                if (!communityTopicRepository.existsByCommunityAndTopic(community, topic)) {
                    val newCommunityTopic = CommunityTopic(community, topic)
                    communityTopicRepository.save(newCommunityTopic)
                } else {
                    val communityTopic = communityTopicRepository.getByCommunityAndTopic(community, topic)
                    if (communityTopic.deleted) communityTopic.deleted = false
                    communityTopicRepository.save(communityTopic)
                }
            }
        }

        if (modifyRequest.name != "") community.name = modifyRequest.name
        if (modifyRequest.description != "") community.description = modifyRequest.description
        community = communityRepository.save(community)
        userCommunityRepository.save(userCommunity)

        return community
    }

    fun deleteCommunity(user: User, communityId: Long): Community {
        if (!communityRepository.existsById(communityId)) throw CommunityNotFoundException()
        var community = communityRepository.getById(communityId)
        if (community.deleted) throw CommunityDeletedException()

        // check whether user is this community's manager
        if (!userCommunityRepository.existsByUserAndCommunity(user, community)) throw NotCommunityManagerException()
        val userCommunity = userCommunityRepository.getByUserAndCommunity(user, community)
        if (!userCommunity.joined) throw NotCommunityManagerException()
        if (!userCommunity.isManager) throw NotCommunityManagerException()

        community.deleted = true
        community = communityRepository.save(community)
        // what to return?
        return community
    }
}
