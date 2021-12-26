package wafflestudio.team4.reddit.domain.community.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Assertions
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.put
import wafflestudio.team4.reddit.global.util.TestHelper

@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CommunityTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {
    private val testHelper = TestHelper(objectMapper)

    private val username1 = "username1"
    private val username2 = "username2"
    private val username3 = "username3"
    private val password = "password"

    // don't use mockBean (test Topic at same time)
    private val topicName1 = "topic1"
    private val topicName2 = "topic2"

    private val communityName1 = "communityName1"
    private val communityName2 = "communityName2"
    private val description = "description"

    // dependencies (mock bean)

    /*@MockBean
    private lateinit var topicService: TopicService

    @MockBean
    private lateinit var topicRepository: TopicRepository
    */

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

    private fun createTopic(authentication: String?, body: String): ResultActionsDsl {
        return mockMvc.post("/api/v1/topics/") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
    }

    private fun createTopicRequest(name: String): String {
        return """
            {
                "name": "$name"
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

    private fun leaveCommunity(authentication: String?, communityId: Long): ResultActionsDsl {
        return mockMvc.delete("/api/v1/communities/$communityId/me/") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
        }
    }

    private fun modifyCommunity(authentication: String?, communityId: Long, body: String): ResultActionsDsl {
        return mockMvc.put("/api/v1/communities/$communityId/") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
    }

    private fun modifyCommunityRequest(name: String, description: String, topics: List<String>): String {
        val topic_array = JSONArray(topics)
        return """
            {
                "name": "$name",
                "topics": $topic_array,
                "description": "$description"
            }
        """.trimIndent()
    }

    private fun deleteCommunity(authentication: String?, communityId: Long): ResultActionsDsl {
        return mockMvc.delete("/api/v1/communities/$communityId/") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
        }
    }

    // setup
    @BeforeAll
    fun createUsers() {
        signup(signupRequest("admin", password))
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

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
    @Order(1)
    fun `1_1_커뮤니티 생성_정상`() {
        // without login
        createCommunity(null, createCommunityRequest(communityName1, description, listOf("topic1", "topic2")))
            .andExpect {
                status { isUnauthorized() }
            }

        // with login
        val authenticationAdmin = signinAndGetAuth("admin", password)
        createTopic(authenticationAdmin, createTopicRequest("topic1"))
            .andExpect {
                status { isCreated() }
            }
        createTopic(authenticationAdmin, createTopicRequest("topic2"))
            .andExpect {
                status { isCreated() }
            }

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
    @Order(2)
    fun `1_2_커뮤니티 생성_중복 이름`() {
        /*val authentication1 = signinAndGetAuth(username1, password)
        createCommunity(
            authentication1,
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2"))
        )
            .andExpect {
                status { isCreated() }
            }*/

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
    @Order(3)
    fun `2_1_커뮤니티 구독_매니저_정상`() {
        /*val authentication1 = signinAndGetAuth(username1, password)
        createCommunity(
            authentication1,
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2"))
        )
            .andExpect {
                status { isCreated() }
            }*/

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
    @Order(4)
    fun `2_2_커뮤니티 구독_일반 회원_정상`() {
        /*val authentication1 = signinAndGetAuth(username1, password)
        createCommunity(
            authentication1,
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2"))
        )
            .andExpect {
                status { isCreated() }
            }*/

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
    @Order(5)
    fun `2_3_커뮤니티 구독_해당 커뮤니티 없음`() {
        val authentication1 = signinAndGetAuth(username1, password)
        joinCommunity(authentication1, joinCommunityRequest("member"), 2)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 2, 3))
            }
    }

    @Test
    @Order(6)
    fun `2_4_커뮤니티 구독_이미 구독`() {
        /*val authentication1 = signinAndGetAuth(username1, password)
        createCommunity(
            authentication1,
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2"))
        )
            .andExpect {
                status { isCreated() }
            }*/
        /*val authentication2 = signinAndGetAuth(username2, password)
        joinCommunity(authentication2, joinCommunityRequest("member"), 1)
            .andExpect {
                status { isCreated() }
            }*/
        val authentication2 = signinAndGetAuth(username2, password)
        val authentication3 = signinAndGetAuth(username3, password)
        // manager attempts rejoin as manager
        joinCommunity(authentication2, joinCommunityRequest("manager"), 1)
            .andExpect {
                status { isBadRequest() }
            }

        // manager attempts rejoin as member -> possible?
        joinCommunity(authentication2, joinCommunityRequest("member"), 1)
            .andExpect {
                status { isBadRequest() }
            }

        // member attempts rejoin as manager -> possible??
        joinCommunity(authentication3, joinCommunityRequest("manager"), 1)
            .andExpect {
                status { isBadRequest() }
            }

        // member attempts rejoin as member
        joinCommunity(authentication3, joinCommunityRequest("member"), 1)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 2, 4))
            }
    }

    @Test
    @Order(7)
    fun `3_1_커뮤니티 탈퇴_매니저_정상`() {
        val authentication2 = signinAndGetAuth(username2, password)
        leaveCommunity(authentication2, 1)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 3, 1))
            }
    }

    @Test
    @Order(8)
    fun `3_2_커뮤니티 탈퇴_일반 회원_정상`() {
        val authentication3 = signinAndGetAuth(username3, password)
        leaveCommunity(authentication3, 1)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 3, 2))
            }
    }

    @Test
    @Order(9)
    fun `3_3_커뮤니티 탈퇴_해당 커뮤니티 없음`() {
        val authentication1 = signinAndGetAuth(username1, password)
        leaveCommunity(authentication1, 2)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 3, 3))
            }
    }

    @Test
    @Order(10)
    fun `3_4_커뮤니티 탈퇴_가입한 적 없음`() {
        signup(signupRequest("username4", password))
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }
        val authentication4 = signinAndGetAuth("username4", password)
        leaveCommunity(authentication4, 1)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 3, 4))
            }
    }

    @Test
    @Order(11)
    fun `3_5_커뮤니티 탈퇴_이미 탈퇴함`() {
        val authentication2 = signinAndGetAuth(username2, password)
        leaveCommunity(authentication2, 1)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 3, 5))
            }
    }

    @Test
    @Order(12)
    fun `4_1_커뮤니티 정보 수정_정상`() {

        val authenticationAdmin = signinAndGetAuth("admin", password)
        createTopic(authenticationAdmin, createTopicRequest("topic3"))
        createTopic(authenticationAdmin, createTopicRequest("topic4"))

        val authentication1 = signinAndGetAuth(username1, password)

        val body = modifyCommunityRequest(
            "changedName1",
            "changedDescription",
            listOf("topic1", "topic3", "topic4")
        )

        modifyCommunity(authentication1, 1, body)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 4, 1))
            }
    }

    @Test
    @Order(13)
    fun `4_2_커뮤니티 정보 수정_해당 커뮤니티 없음`() {
        val authentication1 = signinAndGetAuth(username1, password)
        val body = modifyCommunityRequest(
            "changedName2",
            "changedDescription",
            listOf("topic1")
        )
        modifyCommunity(authentication1, 2, body)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 4, 2))
            }
    }

    @Test
    @Order(14)
    fun `4_3_커뮤니티 정보 수정_매니저 아님`() {
        // 현재 구독 중 x
        val authentication2 = signinAndGetAuth(username2, password)
        val body = modifyCommunityRequest(
            "changedName2",
            "changedDescription2",
            listOf("topic1")
        )
        modifyCommunity(authentication2, 1, body)
            .andExpect {
                status { isUnauthorized() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 4, 3))
            }

        // 일반 회원
        val authentication3 = signinAndGetAuth(username3, password)
        joinCommunity(authentication3, joinCommunityRequest("member"), 1)
        val body2 = modifyCommunityRequest(
            "changedName2",
            "changedDescription2",
            listOf("topic1")
        )
        modifyCommunity(authentication3, 1, body2)
            .andExpect {
                status { isUnauthorized() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 4, 3))
            }
    }

    @Test
    @Order(15)
    fun `4_4_커뮤니티 정보 수정_토픽 없음`() {
        val authentication1 = signinAndGetAuth(username1, password)
        val body = modifyCommunityRequest(
            "changedName2",
            "changedDescription2",
            listOf("topic10")
        )
        modifyCommunity(authentication1, 1, body)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 4, 4))
            }
    }

    @Test
    @Order(18)
    fun `5_1_커뮤니티 삭제_정상`() {
        val authentication1 = signinAndGetAuth(username1, password)
        deleteCommunity(authentication1, 1)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 5, 1))
            }
    }

    @Test
    @Order(17)
    fun `5_2_커뮤니티 삭제_커뮤니티 없음`() {
        val authentication1 = signinAndGetAuth(username1, password)
        deleteCommunity(authentication1, 2)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 5, 2))
            }
    }

    @Test
    @Order(16)
    fun `5_3_커뮤니티 삭제_매니저 아님`() {

        val authentication2 = signinAndGetAuth(username2, password)
        deleteCommunity(authentication2, 1)
            .andExpect {
                status { isUnauthorized() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 5, 3))
            }
    }
}
