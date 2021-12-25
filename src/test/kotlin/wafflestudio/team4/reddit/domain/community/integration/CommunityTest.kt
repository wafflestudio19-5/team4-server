package wafflestudio.team4.reddit.domain.community.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import wafflestudio.team4.reddit.domain.community.service.CommunityService
import wafflestudio.team4.reddit.domain.topic.model.Topic
import wafflestudio.team4.reddit.domain.topic.repository.TopicRepository
import wafflestudio.team4.reddit.domain.topic.service.TopicService
import wafflestudio.team4.reddit.global.util.TestHelper

@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class CommunityTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @Autowired
    private val communityService: CommunityService
) {
    private val testHelper = TestHelper(objectMapper)

    private val username1 = "username1"
    private val username2 = "username2"
    private val username3 = "username3"
    private val password = "password"

    private val topic1: Topic = Topic("topic1")
    private val topic2: Topic = Topic("topic2")

    private val communityName1 = "communityName1"
    private val communityName2 = "communityName2"
    private val description = "description"

    // dependencies (mock bean)

    @MockBean
    private lateinit var topicService: TopicService

    @MockBean
    private lateinit var topicRepository: TopicRepository

    /*private fun createTopic(name: String): ResultActionsDsl {
        return mockMvc.post("/api/v1/topics/"){
            content = """
                "name": "$name"
            """.trimIndent()
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
    }*/

    private fun signin(username: String, password: String): ResultActionsDsl {
        return mockMvc.post("/api/v1/users/signin/") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content =
                """
                    {
                        "email": "${testHelper.toEmail(username)}",
                        "password": "$password"
                    }
                """.trimIndent()
        }
    }

    private fun signup(body: String): ResultActionsDsl {
        return mockMvc.post("/api/v1/users/") {
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
    }

    private fun signinAndGetAuth(username: String, password: String): String {
        return signin(username, password)
            .andReturn()
            .response
            .getHeader("Authentication")!!
    }

    private fun signupRequest(username: String, password: String): String {
        return """
            {
                "email": "$username@snu.ac.kr",
                "username": "$username",
                "password": "$password"
            }
        """.trimIndent()
    }

    // test target
    private fun createCommunity(authentication: String?, body: String): ResultActionsDsl {
        return mockMvc.post("/api/v1/communities/") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
    }

    private fun createCommunityRequest(name: String, description: String, topics: List<String>): String {
        val topic_array = JSONArray(topics)
        return """
            {
                "name": "$name",
                "description": "$description",
                "topics": $topic_array
            }
        """.trimIndent()
    }

    private fun joinCommunity(authentication: String?, body: String, communityId: Long): ResultActionsDsl {
        return mockMvc.post("/api/v1/communities/$communityId/me/") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
    }

    private fun joinCommunityRequest(role: String): String {
        return """
            {
                "role": "$role"
            }
        """.trimIndent()
    }

    // setup
    @BeforeAll
    fun createUsers() {
        signup(signupRequest(username1, password))
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        signup(signupRequest(username2, password))
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        signup(signupRequest(username3, password))
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }
    }

    @Test
    @Transactional
    fun `1_1_커뮤니티 생성_정상`() {
        // without login
        createCommunity(null, createCommunityRequest(communityName1, description, listOf("topic1", "topic2")))
            .andExpect {
                status { isUnauthorized() }
            }

        // with login
        val authentication1 = signinAndGetAuth(username1, password)
        createCommunity(
            authentication1,
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2"))
        )
            .andExpect {
                status { isCreated() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 1, 1))
            }
    }

    @Test
    @Transactional
    fun `1_2_커뮤니티 생성_중복 이름`() {
        val authentication1 = signinAndGetAuth(username1, password)
        createCommunity(
            authentication1,
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2"))
        )
            .andExpect {
                status { isCreated() }
            }

        val authentication2 = signinAndGetAuth(username2, password)
        createCommunity(
            authentication2,
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2"))
        )
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 1, 2))
            }
    }

    @Test
    @Transactional
    fun `2_1_커뮤니티 구독_매니저_정상`() {
        val authentication1 = signinAndGetAuth(username1, password)
        createCommunity(
            authentication1,
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2"))
        )
            .andExpect {
                status { isCreated() }
            }

        val authentication2 = signinAndGetAuth(username2, password)
        joinCommunity(authentication2, joinCommunityRequest("manager"), 1)
            .andExpect {
                status { isCreated() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 2, 1))
            }
    }

    @Test
    @Transactional
    fun `2_2_커뮤니티 구독_일반 회원_정상`() {
        val authentication1 = signinAndGetAuth(username1, password)
        createCommunity(
            authentication1,
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2"))
        )
            .andExpect {
                status { isCreated() }
            }

        val authentication3 = signinAndGetAuth(username3, password)
        joinCommunity(authentication3, joinCommunityRequest("member"), 1)
            .andExpect {
                status { isCreated() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 2, 2))
            }
    }

    @Test
    @Transactional
    fun `2_3_커뮤니티 구독_해당 커뮤니티 없음`() {
        val authentication1 = signinAndGetAuth(username1, password)
        joinCommunity(authentication1, joinCommunityRequest("member"), 1)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 2, 3))
            }
    }

    @Test
    @Transactional
    fun `2_4_커뮤니티 구독_이미 구독`() {
        val authentication1 = signinAndGetAuth(username1, password)
        createCommunity(
            authentication1,
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2"))
        )
            .andExpect {
                status { isCreated() }
            }
        val authentication2 = signinAndGetAuth(username2, password)
        joinCommunity(authentication2, joinCommunityRequest("member"), 1)
            .andExpect {
                status { isCreated() }
            }

        // manager attempts rejoin as manager
        joinCommunity(authentication1, joinCommunityRequest("manager"), 1)
            .andExpect {
                status { isBadRequest() }
            }

        // manager attempts rejoin as member -> possible?
        joinCommunity(authentication1, joinCommunityRequest("member"), 1)
            .andExpect {
                status { isBadRequest() }
            }

        // member attempts rejoin as manager -> possible??
        joinCommunity(authentication2, joinCommunityRequest("manager"), 1)
            .andExpect {
                status { isBadRequest() }
            }

        // member attempts rejoin as member
        joinCommunity(authentication2, joinCommunityRequest("member"), 1)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 2, 4))
            }
    }

    // login x needed?

    @Test
    @Transactional
    fun `3_1_커뮤니티 탈퇴_정상`() {
    }

    @Test
    @Transactional
    fun `3_2_커뮤니티 탈퇴_`() {
    }

    @Test
    @Transactional
    fun `4_1_커뮤니티 정보 수정_정상`() {
    }
}
