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
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActionsDsl
import wafflestudio.team4.reddit.domain.user.repository.UserRepository
import wafflestudio.team4.reddit.global.util.TestHelper

@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CommunityTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val userRepository: UserRepository
) {
    private val testHelper = TestHelper(mockMvc, objectMapper)

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

    private fun createTopic(body: String, authentication: String?): ResultActionsDsl {
        return testHelper.post("/topics/", body, authentication)
    }

    private fun createTopicRequest(name: String): String {
        return """
            {
                "name": "$name"
            }
        """.trimIndent()
    }

    private fun getUserIdByEmail(email: String): Long {
        val user = userRepository.findByEmail(email)
        if (user == null) return -1
        else return user.id
    }

    // test target
    private fun createCommunity(body: String, authentication: String?): ResultActionsDsl {
        return testHelper.post("/communities/", body, authentication)
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

    private fun joinCommunity(communityId: Long, authentication: String?): ResultActionsDsl {
        return testHelper.post("/communities/$communityId/me/", null, authentication)
    }

    private fun leaveCommunity(communityId: Long, authentication: String?): ResultActionsDsl {
        return testHelper.delete("/communities/$communityId/me/", null, authentication)
    }

    private fun modifyCommunityDescription(communityId: Long, body: String, authentication: String?): ResultActionsDsl {
        return testHelper.put("/communities/$communityId/about/description/", body, authentication)
    }

    private fun modifyCommunityDescriptionRequest(description: String): String {
        return """
            {
                "description": "$description"
            }
        """.trimIndent()
    }

    private fun addCommunityManager(communityId: Long, userId: Long, authentication: String?): ResultActionsDsl {
        return testHelper.put("/communities/$communityId/about/moderators/$userId/add/", null, authentication)
    }

    private fun deleteCommunityManager(communityId: Long, userId: Long, authentication: String?): ResultActionsDsl {
        return testHelper.put("/communities/$communityId/about/moderators/$userId/delete/", null, authentication)
    }

    private fun changeCommunityTopic(communityId: Long, topicId: Long, authentication: String?): ResultActionsDsl {
        return testHelper.put("/communities/$communityId/about/topics/$topicId/", null, authentication)
    }

    private fun deleteCommunity(communityId: Long, authentication: String?): ResultActionsDsl {
        return testHelper.delete("/communities/$communityId/", null, authentication)
    }

    private fun compare(mvcResult: MvcResult, testNum: Int, subTestNum: Int): Boolean {
        return testHelper.compare(CommunityTestAnswer, mvcResult, testNum, subTestNum)
    }

    // setup
    @BeforeAll
    fun createUsers() {
        testHelper.signup("admin", password)
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        testHelper.signup(usernameA, password)
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        testHelper.signup(usernameB, password)
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        testHelper.signup(usernameC, password)
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        testHelper.signup(usernameD, password)
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        val authenticationAdmin = testHelper.signinAndGetAuth("admin", password)
        createTopic(createTopicRequest(topicName1), authenticationAdmin)
            .andExpect {
                status { isCreated() }
            }
        createTopic(createTopicRequest(topicName2), authenticationAdmin)
            .andExpect {
                status { isCreated() }
            }
    }

    @Test
    @Order(1)
    fun `1_1_커뮤니티 생성_정상`() {
        // without login
        createCommunity(
            createCommunityRequest(communityName1, description, listOf(topicName1, topicName2)),
            null
        )
            .andExpect {
                status { isUnauthorized() }
            }

        // with login
        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)
        createCommunity(
            createCommunityRequest(communityName1, description, listOf(topicName1, topicName2)),
            authentication1
        )
            .andExpect {
                status { isCreated() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 1, 1))
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

        val authentication2 = testHelper.signinAndGetAuth(usernameB, password)
        createCommunity(
            createCommunityRequest(communityName1, description, listOf(topicName1, topicName2)),
            authentication2
        )
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 1, 2))
            }
    }

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
        val authentication3 = testHelper.signinAndGetAuth(usernameC, password)
        joinCommunity(1, authentication3)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 2, 1))
            }
    }

    @Test
    @Order(4)
    fun `2_2_커뮤니티 구독_해당 커뮤니티 없음`() {
        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)
        joinCommunity(2, authentication1)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 2, 2))
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

        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)
        val authentication3 = testHelper.signinAndGetAuth(usernameC, password)

        joinCommunity(1, authentication1)
            .andExpect {
                status { isBadRequest() }
            }

        // member attempts rejoin as member
        joinCommunity(1, authentication3)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 2, 3))
            }
    }

    @Test
    @Order(6)
    fun `3_1_커뮤니티 설명글 수정_정상`() {

        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)

        val body = modifyCommunityDescriptionRequest(changedDescription)

        modifyCommunityDescription(1, body, authentication1)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 3, 1))
            }
    }

    @Test
    @Order(7)
    fun `3_2_커뮤니티 매니저 추가_정상`() {
        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)
        val cId = getUserIdByEmail(testHelper.toEmail(usernameC))
        addCommunityManager(1, cId, authentication1) // C originally just member
            .andExpect {
                status { isOk() }
            }

        val dId = getUserIdByEmail(testHelper.toEmail(usernameD))
        addCommunityManager(1, dId, authentication1) // D
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 3, 2))
            }
    }

    @Test
    @Order(8)
    fun `3_3_커뮤니티 매니저 삭제_정상`() {
        // 현재 구독 중 x
        val authentication3 = testHelper.signinAndGetAuth(usernameC, password)
        // TODO block deleting oneself

        val aId = getUserIdByEmail(testHelper.toEmail(usernameA))
        deleteCommunityManager(1, aId, authentication3)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 3, 3))
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

        val authenticationAdmin = testHelper.signinAndGetAuth(admin, password)
        createTopic(createTopicRequest("topicName3"), authenticationAdmin)
            .andExpect {
                status { isCreated() }
            }

        val authentication3 = testHelper.signinAndGetAuth(usernameC, password)
        changeCommunityTopic(1, 3, authentication3)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 3, 4))
            }
    }

    @Test
    @Order(10)
    fun `4_1_커뮤니티 탈퇴_일반 회원_정상`() {
        // add member
        val authentication2 = testHelper.signinAndGetAuth(usernameB, password)
        joinCommunity(1, authentication2)

        // leave
        leaveCommunity(1, authentication2)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 4, 1))
            }
    }

    @Test
    @Order(11)
    fun `4_2_커뮤니티 탈퇴_해당 커뮤니티 없음`() {
        val authentication3 = testHelper.signinAndGetAuth(usernameC, password)
        leaveCommunity(2, authentication3)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 4, 2))
            }
    }

    @Test
    @Order(12)
    fun `4_3_커뮤니티 탈퇴_가입한 적 없음`() {
        testHelper.signup(testHelper.signupRequest("usernameE", password))
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }
        val authentication5 = testHelper.signinAndGetAuth("usernameE", password)
        leaveCommunity(1, authentication5)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 4, 3))
            }
    }

    @Test
    @Order(13)
    fun `4_4_커뮤니티 탈퇴_이미 탈퇴함`() {
        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)
        leaveCommunity(1, authentication1)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 4, 4))
            }
    }

    @Test
    @Order(16)
    fun `5_1_커뮤니티 삭제_정상`() {
        val authentication3 = testHelper.signinAndGetAuth(usernameC, password)
        deleteCommunity(1, authentication3)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 5, 1))
            }
    }

    @Test
    @Order(15)
    fun `5_2_커뮤니티 삭제_커뮤니티 없음`() {
        val authentication3 = testHelper.signinAndGetAuth(usernameC, password)
        deleteCommunity(2, authentication3)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 5, 2))
            }
    }

    @Test
    @Order(14)
    fun `5_3_커뮤니티 삭제_매니저 아님`() {

        val authentication2 = testHelper.signinAndGetAuth(usernameB, password)
        deleteCommunity(1, authentication2)
            .andExpect {
                status { isUnauthorized() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 5, 3))
            }
    }
}
