package wafflestudio.team4.reddit.domain.community.service

import org.springframework.data.domain.Page
// import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wafflestudio.team4.reddit.domain.community.dto.CommunityDto
import wafflestudio.team4.reddit.domain.community.exception.CommunityNotFoundException
import wafflestudio.team4.reddit.domain.community.exception.CommunityDeletedException
import wafflestudio.team4.reddit.domain.community.exception.CommunityAlreadyExistsException
import wafflestudio.team4.reddit.domain.community.exception.AlreadyJoinedException
import wafflestudio.team4.reddit.domain.community.exception.NotCurrentlyJoinedException
import wafflestudio.team4.reddit.domain.community.exception.NotCommunityManagerException
import wafflestudio.team4.reddit.domain.community.exception.AlreadyManagerException
import wafflestudio.team4.reddit.domain.community.model.Community
import wafflestudio.team4.reddit.domain.community.model.CommunityTopic
import wafflestudio.team4.reddit.domain.community.model.UserCommunity
import wafflestudio.team4.reddit.domain.community.repository.CommunityRepository
import wafflestudio.team4.reddit.domain.community.repository.CommunityTopicRepository
import wafflestudio.team4.reddit.domain.community.repository.UserCommunityRepository
import wafflestudio.team4.reddit.domain.post.model.Post
import wafflestudio.team4.reddit.domain.post.repository.PostRepository
import wafflestudio.team4.reddit.domain.topic.exceptions.TopicNotFoundException
import wafflestudio.team4.reddit.domain.topic.model.Topic
import wafflestudio.team4.reddit.domain.topic.service.TopicService
import wafflestudio.team4.reddit.domain.user.exception.UserNotFoundException
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.repository.UserRepository
import wafflestudio.team4.reddit.domain.user.service.UserService
import java.time.LocalDateTime

@Service
class CommunityService(
    private val communityRepository: CommunityRepository,
    private val userCommunityRepository: UserCommunityRepository,
    private val communityTopicRepository: CommunityTopicRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val topicService: TopicService,
    private val userService: UserService
) {

    // used in search query
    fun getCommunitiesPage(lastCommunityId: Long, size: Int, topicId: Long): Page<Community> {
        val pageRequest = Pageable.ofSize(size)
        if (topicId == -1L) return communityRepository.findByIdLessThanAndDeletedFalseOrderByIdDesc(
            lastCommunityId,
            pageRequest
        )

        val communityTopics = communityTopicRepository.findByTopicIdEqualsAndDeletedFalse(topicId)
        val communityIds = mutableListOf<Long>()
        for (communityTopic in communityTopics) {
            communityIds.add(communityTopic.community.id)
        }

        return communityRepository.findByIdInAndIdLessThanAndDeletedFalseOrderByIdDesc(
            communityIds,
            lastCommunityId,
            pageRequest
        )
    }

    // lastCommunityIds for each of community page links
    fun getCommunityLinkIds(lastCommunityId: Long, size: Int, topicId: Long): List<Long> {
        // first, last, next, prev
        val communityTopics = communityTopicRepository.findByTopicIdEqualsAndDeletedFalse(topicId)
        val communityIds = mutableListOf<Long>()
        for (communityTopic in communityTopics) {
            communityIds.add(communityTopic.community.id)
        }
        communityIds.sortDescending()
        val first = communityIds[0] + 1
        val last = if (communityIds.size - size >= 0) communityIds[communityIds.size - size] + 1
        else communityIds[0] + 1
        val indexOflastCommunity = if (lastCommunityId == Long.MAX_VALUE) 0
        else communityIds.indexOf(lastCommunityId - 1)
        val next = communityIds[
            if (indexOflastCommunity + size < communityIds.size)
                indexOflastCommunity + size - 1 else communityIds.size - 1
        ]
        val prev = communityIds[if (indexOflastCommunity - size >= 0) indexOflastCommunity - size else 0] + 1

        return listOf(first, last, next, prev)
    }

    fun getCommunityById(communityId: Long): Community {
        val community = communityRepository.findByIdOrNull(communityId) ?: throw CommunityNotFoundException()
        if (community.deleted) throw CommunityDeletedException()
        return community
    }

    fun getCommunityPosts(communityId: Long, lastPostId: Long, size: Int): List<Post> {
        getCommunityById(communityId) // check whether community with id exists, if not exception
        val pageRequest = Pageable.ofSize(size)
        return postRepository.findByCommunityIdEqualsAndIdLessThanAndDeletedIsFalseOrderByIdDesc(
            communityId,
            lastPostId,
            pageRequest
        ).content
    }

    fun getManagers(communityId: Long): List<User> {
        val community = getCommunityById(communityId)
        val userCommunityList = userCommunityRepository.findAllByCommunity(community)
        val managerCommunityList = mutableListOf<UserCommunity>()
        for (userCommunity in userCommunityList) {
            if (userCommunity.isManager && userCommunity.joined) managerCommunityList.add(userCommunity)
        }
        val managers = managerCommunityList.map { it.user }
        return managers
    }

    fun getTopics(communityId: Long): List<Topic> {
        val community = getCommunityById(communityId)
        val communityTopicList = communityTopicRepository.getAllByCommunity(community)
        val currentCommunityTopicList = mutableListOf<CommunityTopic>()
        for (communityTopic in communityTopicList) {
            if (!communityTopic.deleted) currentCommunityTopicList.add(communityTopic)
        }
        val topics = currentCommunityTopicList.map { it.topic }
        return topics
    }

    fun getDescription(communityId: Long): String {
        val community = getCommunityById(communityId)
        return community.description
    }

    fun createCommunity(createRequest: CommunityDto.CreateRequest, user: User): Community {
        var community = Community(
            name = createRequest.name,
            num_members = 0, // managers not part of num_members
            num_managers = 1,
            description = createRequest.description,
            deleted = false
        )

        if (communityRepository.existsByName(createRequest.name)) {
            val oldCommunity = communityRepository.getByName(createRequest.name)
            if (!oldCommunity.deleted) throw CommunityAlreadyExistsException()
            else {
                oldCommunity.name += ("-deprecated" + LocalDateTime.now() + user.id)
                communityRepository.save(oldCommunity)
                // oldCommunity = community
                // community = communityRepository.save(oldCommunity)
            }
        }

        community = communityRepository.save(community)

        val topics = mutableListOf<Topic>()
        for (topicName in createRequest.topics) {
            if (!topicService.checkTopicExistence(topicName)) throw TopicNotFoundException()
            val topic = topicService.getTopicByName(topicName)
            topics.add(topic)
        }

        // add additional topics
        if (topics.size >= 1) {
            for (i in 0 until topics.size) {
                val newCommunityTopic = CommunityTopic(
                    community = community,
                    topic = topics[i]
                )
                communityTopicRepository.save(newCommunityTopic)
            }
        }

        // creator becomes one of managers
        val userCommunity = UserCommunity(
            user = user,
            community = community,
            isManager = true,
        )
        userCommunityRepository.save(userCommunity)

        return community
    }

    // can only join as member, not manager
    fun joinCommunity(user: User, communityId: Long): Community {
        var community = getCommunityById(communityId) // check whether throws correct exception

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
        var community = getCommunityById(communityId)

        if (!userCommunityRepository.existsByUserAndCommunity(user, community))
            throw NotCurrentlyJoinedException()
        val userCommunity = userCommunityRepository.getByUserAndCommunity(user, community)
        if (!userCommunity.joined) throw NotCurrentlyJoinedException()

        if (userCommunity.isManager) {
            community.num_managers -= 1
            userCommunity.isManager = false
            // val managerCommunity = managerCommunityRepository.getByManagerAndCommunity(user, community)
            // managerCommunity.joined = false
            // managerCommunityRepository.save(managerCommunity)
        } else community.num_members -= 1
        userCommunity.joined = false

        userCommunityRepository.save(userCommunity)
        community = communityRepository.save(community)

        return community
    }

    @Transactional
    fun modifyCommunity(user: User, modifyRequest: CommunityDto.ModifyRequest, communityId: Long):
        Community {
        // check community existence
        var community = getCommunityById(communityId)

        // check if user is this community's manager
        checkManagerStatus(user, community)

        // change description
        if (modifyRequest.description != null) {
            community.description = modifyRequest.description
            communityRepository.save(community)
        }

        // add, delete topics
        // delete newly deleted topics
        // if no already assigned topics, skip this block
        val oldTopics = if (communityTopicRepository.existsByCommunity(community))
            communityTopicRepository.getAllByCommunity(community) else null
        if (modifyRequest.topics != null && oldTopics != null) {
            for (communityTopic in oldTopics) {
                if (communityTopic.topic.name !in modifyRequest.topics) communityTopic.deleted = true
                communityTopicRepository.save(communityTopic)
            }
        }

        // add newly added topics
        if (modifyRequest.topics != null && modifyRequest.topics.isNotEmpty()) {
            for (topicName in modifyRequest.topics) {
                // check if topic is an existing topic
                if (!topicService.checkTopicExistence(topicName)) throw TopicNotFoundException()
                val topic = topicService.getTopicByName(topicName)
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

        // add, delete managers
        val oldManagers = userCommunityRepository.findAllByCommunity(community)

        // delete newly deleted managers
        if (modifyRequest.managers != null && oldManagers.isNotEmpty()) {
            for (managerCommunity in oldManagers) {
                /*if (managerCommunity.user.email !in modifyRequest.managers) managerCommunity.joined = false
                userCommunityRepository.save(managerCommunity)*/
                if (managerCommunity.user.email !in modifyRequest.managers)
                    community = deleteManager(community, managerCommunity.user)
            }
        }

        // add newly added managers
        if (modifyRequest.managers != null && modifyRequest.managers.isNotEmpty()) {
            for (managerEmail in modifyRequest.managers) {
                // check if user with email (managerEmail) exists
                val manager = userRepository.findByEmail(managerEmail) ?: throw UserNotFoundException()
                /*if (!userCommunityRepository.existsByUserAndCommunity(manager, community)) {
                    val newUserCommunity = UserCommunity(manager, community)
                    userCommunityRepository.save(newUserCommunity)
                } else {
                    val userCommunity = userCommunityRepository.getByUserAndCommunity(manager, community)
                    if (!userCommunity.joined) {
                        userCommunity.joined = true
                        userCommunityRepository.save(userCommunity)
                    }
                }*/
                community = addManager(community, manager)
            }
        }
        return community
    }

    fun modifyCommunityDescription(user: User, modifyRequest: CommunityDto.ModifyDescriptionRequest, communityId: Long):
        Community {
        // check community existence
        var community = getCommunityById(communityId)

        // check if user is this community's manager
        checkManagerStatus(user, community)

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

        // change description
        community.description = modifyRequest.description
        community = communityRepository.save(community)

        return community
    }

    fun addCommunityManager(user: User, communityId: Long, userId: Long): Community {
        // check community existence -> necessary?
        var community = getCommunityById(communityId)

        // check if user is this community's manager
        checkManagerStatus(user, community)

        val manager = userService.getUserById(userId)
        community = addManager(community, manager)
        return community
    }

    fun deleteCommunityManager(user: User, communityId: Long, userId: Long): Community {
        // check community existence -> necessary?
        var community = getCommunityById(communityId)

        // check if user is this community's manager -> necessary?
        checkManagerStatus(user, community)

        val manager = userService.getUserById(userId)
        community = deleteManager(community, manager)
        return community
    }

    fun changeCommunityTopic(user: User, communityId: Long, topicId: Long): Community {
        // check community existence
        val community = getCommunityById(communityId)

        // check if user is this community's manager
        checkManagerStatus(user, community)

        val topic = topicService.getTopicById(topicId)

        // topic already added -> toggle to delete / not added -> toggle to add
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
        var community = getCommunityById(communityId)

        // check whether user is this community's manager
        checkManagerStatus(user, community)

        community.deleted = true
        community = communityRepository.save(community)
        // what to return?
        return community
    }

    // helper functions
    fun addManager(community: Community, manager: User): Community {
        /*if (managerCommunityRepository.existsByUserAndCommunity(manager, community)) {
            val managerCommunity = userCommunityRepository.getByManagerAndCommunity(manager, community)
            if (managerCommunity.joined) throw AlreadyManagerException()
            managerCommunity.joined = true // reassign
            managerCommunityRepository.save(managerCommunity)
        } else { // first assign
            val managerCommunity = ManagerCommunity(
                manager = manager,
                community = community
            )
            managerCommunityRepository.save(managerCommunity)
        }*/

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
            // currently joined
            if (userCommunity.joined) {
                if (userCommunity.isManager) throw AlreadyManagerException()
                else community.num_members -= 1
            }
            // currently not joined
            if (!userCommunity.joined) { // rejoin
                userCommunity.joined = true
            }
            userCommunity.isManager = true
            userCommunityRepository.save(userCommunity)
        }

        community.num_managers += 1
        communityRepository.save(community)
        return community
    }

    fun deleteManager(community: Community, manager: User): Community {
        // check if manager
        val managerCommunity = checkManagerStatus(manager, community)

        managerCommunity.joined = false
        managerCommunity.isManager = false
        userCommunityRepository.save(managerCommunity)

        community.num_managers -= 1
        communityRepository.save(community)
        return community
    }

    /*fun changeTopics(community: Community, topics: List<Topic>) {
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
    }*/

    fun checkManagerStatus(user: User, community: Community): UserCommunity {
        if (!userCommunityRepository.existsByUserAndCommunity(user, community))
            throw NotCommunityManagerException()
        val userCommunity = userCommunityRepository.getByUserAndCommunity(user, community)
        if (!userCommunity.joined) throw NotCommunityManagerException()
        if (!userCommunity.isManager) throw NotCommunityManagerException()
        return userCommunity
    }
}
