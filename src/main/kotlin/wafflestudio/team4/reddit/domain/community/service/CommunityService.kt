package wafflestudio.team4.reddit.domain.community.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import wafflestudio.team4.reddit.domain.community.dto.CommunityDto
import wafflestudio.team4.reddit.domain.community.exception.CommunityNotFoundException
import wafflestudio.team4.reddit.domain.community.exception.CommunityDeletedException
import wafflestudio.team4.reddit.domain.community.exception.AlreadyJoinedException
import wafflestudio.team4.reddit.domain.community.exception.CommunityAlreadyExistsException
import wafflestudio.team4.reddit.domain.community.exception.NotCurrentlyJoinedException
import wafflestudio.team4.reddit.domain.community.exception.NotCommunityManagerException
import wafflestudio.team4.reddit.domain.community.exception.AlreadyManagerException
import wafflestudio.team4.reddit.domain.community.model.Community
import wafflestudio.team4.reddit.domain.community.model.CommunityTopic
import wafflestudio.team4.reddit.domain.community.model.ManagerCommunity
import wafflestudio.team4.reddit.domain.community.model.UserCommunity
import wafflestudio.team4.reddit.domain.community.repository.CommunityRepository
import wafflestudio.team4.reddit.domain.community.repository.CommunityTopicRepository
import wafflestudio.team4.reddit.domain.community.repository.ManagerCommunityRepository
import wafflestudio.team4.reddit.domain.community.repository.UserCommunityRepository
import wafflestudio.team4.reddit.domain.topic.model.Topic
import wafflestudio.team4.reddit.domain.topic.service.TopicService
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.service.UserService

@Service
class CommunityService(
    private val communityRepository: CommunityRepository,
    private val userCommunityRepository: UserCommunityRepository,
    private val managerCommunityRepository: ManagerCommunityRepository,
    private val communityTopicRepository: CommunityTopicRepository,
    private val topicService: TopicService,
    private val userService: UserService
) {
    fun getAllCommunities(): List<Community> {
        return communityRepository.findAll()
    }

    // used in search query
    fun getCommunitiesPage(lastCommunityId: Long, size: Int): Page<Community> {
        val pageRequest = PageRequest.of(0, size)
        return communityRepository.findByIdLessThanOrderByIdDesc(lastCommunityId, pageRequest)
    }

    fun getCommunityById(communityId: Long): Community {
        if (!communityRepository.existsById(communityId)) throw CommunityNotFoundException()
        return communityRepository.getById(communityId)
    }

    fun createCommunity(createRequest: CommunityDto.CreateRequest, user: User): Community {
        var community = Community(
            name = createRequest.name,
            num_members = 0, // managers not part of num_members
            num_managers = 1,
            description = "", // createRequest.description,
            deleted = false
        )
        if (communityRepository.existsByName(createRequest.name)) {
            var oldCommunity = communityRepository.getByName(createRequest.name)
            if (!oldCommunity.deleted) throw CommunityAlreadyExistsException()
            else {
                oldCommunity = community
                community = communityRepository.save(oldCommunity)
            }
        } else community = communityRepository.save(community)

        // creator becomes one of managers
        val userCommunity = UserCommunity(
            user = user,
            community = community,
            isManager = true,
        )
        userCommunityRepository.save(userCommunity)

        val managerCommunity = ManagerCommunity(
            manager = user,
            community = community
        )
        managerCommunityRepository.save(managerCommunity)

        // topics not added at first
        /*val topics = mutableListOf<Topic>()
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
        */
        return community
    }

    // can only join as member, not manager
    fun joinCommunity(user: User, communityId: Long): Community {
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
            else userCommunity.joined = true // rejoin
        }

        /*if (role == "manager") {
            userCommunity.isManager = true
            community.num_managers += 1
        } else if (role == "member") community.num_members += 1*/
        community.num_members += 1

        userCommunityRepository.save(userCommunity)
        community = communityRepository.save(community)

        return community
    }

    fun leaveCommunity(user: User, communityId: Long): Community {
        if (!communityRepository.existsById(communityId)) throw CommunityNotFoundException()
        var community = communityRepository.getById(communityId)
        if (community.deleted) throw CommunityDeletedException()

        if (!userCommunityRepository.existsByUserAndCommunity(user, community))
            throw NotCurrentlyJoinedException()
        val userCommunity = userCommunityRepository.getByUserAndCommunity(user, community)
        if (!userCommunity.joined) throw NotCurrentlyJoinedException()

        if (userCommunity.isManager) {
            community.num_managers -= 1
            val managerCommunity = managerCommunityRepository.getByManagerAndCommunity(user, community)
            managerCommunity.joined = false
            managerCommunityRepository.save(managerCommunity)
        } else community.num_members -= 1
        userCommunity.joined = false

        userCommunityRepository.save(userCommunity)
        community = communityRepository.save(community)

        return community
    }

    // split according to options?
    fun modifyCommunityDescription(user: User, modifyRequest: CommunityDto.ModifyRequest, communityId: Long):
        Community {
        // check community existence
        if (!communityRepository.existsById(communityId)) throw CommunityNotFoundException()
        var community = communityRepository.getById(communityId)
        if (community.deleted) throw CommunityDeletedException()

        // check if user is this community's manager
        /*if (!userCommunityRepository.existsByUserAndCommunity(user, community))
        throw NotCommunityManagerException()
        val userCommunity = userCommunityRepository.getByUserAndCommunity(user, community)
        if (!userCommunity.joined) throw NotCommunityManagerException()
        if (!userCommunity.isManager) throw NotCommunityManagerException()
        */
        if (!managerCommunityRepository.existsByManagerAndCommunity(user, community))
            throw NotCommunityManagerException()
        val managerCommunity = managerCommunityRepository.getByManagerAndCommunity(user, community)
        if (!managerCommunity.joined) throw NotCommunityManagerException()

        // add, delete topics
        // delete newly deleted topics
        // if no already assigned topics, skip this block
        /*val oldTopics = if (communityTopicRepository.existsByCommunity(community))
        communityTopicRepository.getAllByCommunity(community) else null
        if (oldTopics != null){
            for (communityTopic in oldTopics) {
                if (communityTopic.topic.name !in modifyRequest.topics) communityTopic.deleted = true
                communityTopicRepository.save(communityTopic)
            }
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
        }*/

        // change name -> x
        // if (modifyRequest.name != "") community.name = modifyRequest.name

        // change description
        if (modifyRequest.description != "") community.description = modifyRequest.description

        community = communityRepository.save(community)

        return community
    }

    fun addCommunityManager(user: User, communityId: Long, userId: Long): Community {
        // check community existence -> necessary?
        if (!communityRepository.existsById(communityId)) throw CommunityNotFoundException()
        var community = communityRepository.getById(communityId)
        if (community.deleted) throw CommunityDeletedException()

        // check if user is this community's manager -> necessary?
        if (!managerCommunityRepository.existsByManagerAndCommunity(user, community))
            throw NotCommunityManagerException()
        val managerCommunity = managerCommunityRepository.getByManagerAndCommunity(user, community)
        if (!managerCommunity.joined) throw NotCommunityManagerException()

        val manager = userService.getUserById(userId)
        community = addManager(community, manager)
        communityRepository.save(community)
        return community
    }

    fun deleteCommunityManager(user: User, communityId: Long, userId: Long): Community {
        // check community existence -> necessary?
        if (!communityRepository.existsById(communityId)) throw CommunityNotFoundException()
        var community = communityRepository.getById(communityId)
        if (community.deleted) throw CommunityDeletedException()

        // check if user is this community's manager -> necessary?
        if (!managerCommunityRepository.existsByManagerAndCommunity(user, community))
            throw NotCommunityManagerException()
        val managerCommunity = managerCommunityRepository.getByManagerAndCommunity(user, community)
        if (!managerCommunity.joined) throw NotCommunityManagerException()

        val manager = userService.getUserById(userId)
        community = deleteManager(community, manager)
        communityRepository.save(community)
        return community
    }

    fun changeCommunityTopic(user: User, communityId: Long, topicId: Long): Community {
        // check community existence
        if (!communityRepository.existsById(communityId)) throw CommunityNotFoundException()
        var community = communityRepository.getById(communityId)
        if (community.deleted) throw CommunityDeletedException()

        // check if user is this community's manager
        if (!managerCommunityRepository.existsByManagerAndCommunity(user, community))
            throw NotCommunityManagerException()
        val managerCommunity = managerCommunityRepository.getByManagerAndCommunity(user, community)
        if (!managerCommunity.joined) throw NotCommunityManagerException()

        val topic = topicService.getTopicById(topicId)

        // topic already added -> toggle to delete, not added -> toggle to add
        if (communityTopicRepository.existsByCommunityAndTopic(community, topic)) {
            val communityTopic = communityTopicRepository.getByCommunityAndTopic(community, topic)
            communityTopic.deleted = !communityTopic.deleted
            communityTopicRepository.save(communityTopic)
        } else {
            val newCommunityTopic = CommunityTopic(community, topic)
            communityTopicRepository.save(newCommunityTopic)
        }
        return community
    }

    fun deleteCommunity(user: User, communityId: Long): Community {
        // check community existence
        if (!communityRepository.existsById(communityId)) throw CommunityNotFoundException()
        var community = communityRepository.getById(communityId)
        if (community.deleted) throw CommunityDeletedException()

        // check whether user is this community's manager
        /*if (!userCommunityRepository.existsByUserAndCommunity(user, community))
        throw NotCommunityManagerException()
        val userCommunity = userCommunityRepository.getByUserAndCommunity(user, community)
        if (!userCommunity.joined) throw NotCommunityManagerException()
        if (!userCommunity.isManager) throw NotCommunityManagerException()
        */
        if (!managerCommunityRepository.existsByManagerAndCommunity(user, community))
            throw NotCommunityManagerException()
        val managerCommunity = managerCommunityRepository.getByManagerAndCommunity(user, community)
        if (!managerCommunity.joined) throw NotCommunityManagerException()

        community.deleted = true
        community = communityRepository.save(community)
        // what to return?
        return community
    }

    // helper functions
    fun addManager(community: Community, manager: User): Community { // don't have to accept invitation
        if (managerCommunityRepository.existsByManagerAndCommunity(manager, community)) {
            val managerCommunity = managerCommunityRepository.getByManagerAndCommunity(manager, community)
            if (managerCommunity.joined) throw AlreadyManagerException()
            managerCommunity.joined = true // reassign
            managerCommunityRepository.save(managerCommunity)
        } else { // first assign
            val managerCommunity = ManagerCommunity(
                manager = manager,
                community = community
            )
            managerCommunityRepository.save(managerCommunity)
        }
        // if not user, add as user
        if (!userCommunityRepository.existsByUserAndCommunity(manager, community)) { // first join
            val userCommunity = UserCommunity(
                user = manager,
                community = community,
                isManager = true
            )
            userCommunityRepository.save(userCommunity)
        } else {
            val userCommunity = userCommunityRepository.getByUserAndCommunity(manager, community)
            if (!userCommunity.joined) { // rejoin
                userCommunity.joined = true
                userCommunity.isManager = true
                userCommunityRepository.save(userCommunity)
            }
        }

        community.num_managers += 1
        return community
    }

    fun deleteManager(community: Community, manager: User): Community {
        // check if manager
        if (!managerCommunityRepository.existsByManagerAndCommunity(manager, community))
            throw NotCommunityManagerException()
        val managerCommunity = managerCommunityRepository.getByManagerAndCommunity(manager, community)
        if (!managerCommunity.joined) throw NotCommunityManagerException()

        managerCommunity.joined = false
        managerCommunityRepository.save(managerCommunity)
        val userCommunity = userCommunityRepository.getByUserAndCommunity(manager, community)
        userCommunity.isManager = false
        userCommunityRepository.save(userCommunity)

        community.num_managers -= 1
        return community
    }

    fun changeTopics(community: Community, topics: List<Topic>) {
        // delete newly deleted topics
        // if no already assigned topics, skip this block
        val oldTopics = if (communityTopicRepository.existsByCommunity(community))
            communityTopicRepository.getAllByCommunity(community) else null
        if (oldTopics != null) {
            for (communityTopic in oldTopics) {
                if (communityTopic.topic !in topics) communityTopic.deleted = true
                communityTopicRepository.save(communityTopic)
            }
        }

        // add newly added topics
        if (!topics.isEmpty()) {
            for (topic in topics) {
                if (!communityTopicRepository.existsByCommunityAndTopic(community, topic)) {
                    val newCommunityTopic = CommunityTopic(community, topic)
                    communityTopicRepository.save(newCommunityTopic)
                } else {
                    val communityTopic = communityTopicRepository.getByCommunityAndTopic(community, topic)
                    if (communityTopic.deleted) {
                        communityTopic.deleted = false
                        communityTopicRepository.save(communityTopic)
                    }
                }
            }
        }
    }
}
