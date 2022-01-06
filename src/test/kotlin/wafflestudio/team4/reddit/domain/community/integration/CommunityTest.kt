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
// import org.springframework.http.ResponseEntity
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.put
// import org.springframework.web.bind.annotation.PathVariable
// import org.springframework.web.bind.annotation.PutMapping
// import wafflestudio.team4.reddit.domain.community.dto.CommunityDto
// import wafflestudio.team4.reddit.domain.user.model.User
// import wafflestudio.team4.reddit.global.auth.CurrentUser
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

    private val admin = "admin"
    private val usernameA = "usernameA"
    private val usernameB = "usernameB"
    private val usernameC = "usernameC"
    private val usernameD = "usernameD"
    private val password = "password"

    private val communityName1 = "communityName1"
    private val communityName2 = "communityName2"
    private val description = "description"
    private val changedDescription = "changedDescription"

    private val topicName1 = "topicName1"
    private val topicName2 = "topicName2"
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

    private fun joinCommunity(authentication: String?, /*body: String, */communityId: Long): ResultActionsDsl {
        return mockMvc.post("/api/v1/communities/$communityId/me/") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
            // content = (body)
            // contentType = (MediaType.APPLICATION_JSON)
            // accept = (MediaType.APPLICATION_JSON)
        }
    }

    /*private fun joinCommunityRequest(role: String): String {
        return """
            {
                "role": "$role"
            }
        """.trimIndent()
    }*/

    private fun leaveCommunity(authentication: String?, communityId: Long): ResultActionsDsl {
        return mockMvc.delete("/api/v1/communities/$communityId/me/") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
        }
    }

    private fun modifyCommunityDescription(authentication: String?, communityId: Long, body: String): ResultActionsDsl {
        return mockMvc.put("/api/v1/communities/$communityId/about/description/") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
    }

    private fun modifyCommunityDescriptionRequest(description: String): String {
        return """
            {
                "description": "$description"
            }
        """.trimIndent()
    }

    private fun addCommunityManager(authentication: String?, communityId: Long, userId: Long): ResultActionsDsl {
        return mockMvc.put("/api/v1/communities/$communityId/about/moderators/$userId/add/") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
        }
    }

    private fun deleteCommunityManager(authentication: String?, communityId: Long, userId: Long): ResultActionsDsl {
        return mockMvc.put("/api/v1/communities/$communityId/about/moderators/$userId/delete/") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
        }
    }

    private fun changeCommunityTopic(authentication: String?, communityId: Long, topicId: Long): ResultActionsDsl {
        return mockMvc.put("/api/v1/communities/$communityId/about/topics/$topicId/") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
        }
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
        signup(signupRequest(admin, password)) // id 3
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        signup(signupRequest(usernameA, password)) // id 4
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        signup(signupRequest(usernameB, password)) // id 5
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        signup(signupRequest(usernameC, password)) // id 6
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        signup(signupRequest(usernameD, password)) // id 7
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        val authentication = signinAndGetAuth(admin, password)
        createTopic(authentication, createTopicRequest(topicName1))
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }
        createTopic(authentication, createTopicRequest(topicName2))
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }
    }

    @Test
    @Order(1)
    fun `1_1_커뮤니티 생성_정상`() {
        // without login
        createCommunity(null, createCommunityRequest(communityName1, description, listOf(topicName1, topicName2)))
            .andExpect {
                status { isUnauthorized() }
            }

        val authentication1 = signinAndGetAuth(usernameA, password)
        createCommunity(
            authentication1,
            createCommunityRequest(communityName1, description, listOf(topicName1, topicName2))
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

        val authentication2 = signinAndGetAuth(usernameB, password)
        createCommunity(
            authentication2,
            createCommunityRequest(communityName1, description, listOf(topicName1, topicName2))
        )
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 1, 2))
            }
    }

    /*@Test
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

        val authentication2 = signinAndGetAuth(usernameB, password)
        joinCommunity(authentication2, 1)
            .andExpect {
                status { isCreated() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 2, 1))
            }
    }
    */

    @Test
    @Order(3)
    fun `2_1_커뮤니티 구독_정상`() {
        /*val authentication1 = signinAndGetAuth(username1, password)
        createCommunity(
            authentication1,
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2"))
        )
            .andExpect {
                status { isCreated() }
            }*/
        val authentication3 = signinAndGetAuth(usernameC, password)
        joinCommunity(authentication3, 1)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 2, 1))
            }
    }

    @Test
    @Order(4)
    fun `2_2_커뮤니티 구독_해당 커뮤니티 없음`() {
        val authentication1 = signinAndGetAuth(usernameA, password)
        joinCommunity(authentication1, 2)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 2, 2))
            }
    }

    @Test
    @Order(5)
    fun `2_3_커뮤니티 구독_이미 구독`() {
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
        val authentication1 = signinAndGetAuth(usernameA, password)
        val authentication3 = signinAndGetAuth(usernameC, password)
        // manager attempts rejoin as manager
        /*joinCommunity(authentication2, 1)
            .andExpect {
                status { isBadRequest() }
            }*/

        // manager attempts rejoin as member
        joinCommunity(authentication1, 1)
            .andExpect {
                status { isBadRequest() }
            }

        // member attempts rejoin as manager -> possible??
        /*joinCommunity(authentication3, 1)
            .andExpect {
                status { isBadRequest() }
            }*/

        // member attempts rejoin as member
        joinCommunity(authentication3, 1)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 2, 3))
            }
    }

    @Test
    @Order(6)
    fun `3_1_커뮤니티 설명글 수정_정상`() {

        val authentication1 = signinAndGetAuth(usernameA, password)

        val body = modifyCommunityDescriptionRequest(changedDescription)

        modifyCommunityDescription(authentication1, 1, body)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 3, 1))
            }
    }

    @Test
    @Order(7)
    fun `3_2_커뮤니티 매니저 추가_정상`() {
        val authentication1 = signinAndGetAuth(usernameA, password)

        addCommunityManager(authentication1, 1, 6) // C originally just member
            .andExpect {
                status { isOk() }
            }

        addCommunityManager(authentication1, 1, 7) // D
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 3, 2))
            }
    }

    @Test
    @Order(8)
    fun `3_3_커뮤니티 매니저 삭제_정상`() {
        // 현재 구독 중 x
        val authentication3 = signinAndGetAuth(usernameC, password)
        // TODO block deleting oneself

        deleteCommunityManager(authentication3, 1, 3)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 3, 3))
            }
    }

    @Test
    @Order(9)
    fun `3_4_커뮤니티 토픽 추가_정상`() {
        // with login
        /*val authenticationAdmin = signinAndGetAuth("admin", password)
        createTopic(authenticationAdmin, createTopicRequest("topic1"))
            .andExpect {
                status { isCreated() }
            }
        createTopic(authenticationAdmin, createTopicRequest("topic2"))
            .andExpect {
                status { isCreated() }
            }*/

        val authenticationAdmin = signinAndGetAuth(admin, password)
        createTopic(authenticationAdmin, createTopicRequest("topicName3"))
            .andExpect {
                status { isCreated() }
            }

        val authentication3 = signinAndGetAuth(usernameC, password)
        changeCommunityTopic(authentication3, 1, 3)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 3, 4))
            }
    }

    /*@Test
    @Order(15)
    fun `4_4_커뮤니티 정보 수정_토픽 없음`() {
        val authentication1 = signinAndGetAuth(usernameA, password)
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
    }*/

    @Test
    @Order(10)
    fun `4_1_커뮤니티 탈퇴_일반 회원_정상`() {
        // add member
        val authentication2 = signinAndGetAuth(usernameB, password)
        joinCommunity(authentication2, 1)

        // leave
        leaveCommunity(authentication2, 1)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 4, 1))
            }
    }

    @Test
    @Order(11)
    fun `4_2_커뮤니티 탈퇴_해당 커뮤니티 없음`() {
        val authentication3 = signinAndGetAuth(usernameC, password)
        leaveCommunity(authentication3, 2)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 4, 2))
            }
    }

    @Test
    @Order(12)
    fun `4_3_커뮤니티 탈퇴_가입한 적 없음`() {
        signup(signupRequest("usernameE", password))
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }
        val authentication5 = signinAndGetAuth("usernameE", password)
        leaveCommunity(authentication5, 1)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 4, 3))
            }
    }

    @Test
    @Order(13)
    fun `4_4_커뮤니티 탈퇴_이미 탈퇴함`() {
        val authentication1 = signinAndGetAuth(usernameA, password)
        leaveCommunity(authentication1, 1)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 4, 4))
            }
    }

    @Test
    @Order(14)
    fun `5_1_커뮤니티 삭제_정상`() {
        val authentication3 = signinAndGetAuth(usernameC, password)
        deleteCommunity(authentication3, 1)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(testHelper.compareCommunity(mvcResult, 5, 1))
            }
    }

    @Test
    @Order(15)
    fun `5_2_커뮤니티 삭제_커뮤니티 없음`() {
        val authentication3 = signinAndGetAuth(usernameC, password)
        deleteCommunity(authentication3, 2)
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

        val authentication2 = signinAndGetAuth(usernameB, password)
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
