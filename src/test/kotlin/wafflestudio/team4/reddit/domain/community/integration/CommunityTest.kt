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
    private val testHelper = TestHelper(mockMvc, objectMapper)

    private val usernameA = "usernameA"
    private val usernameB = "usernameB"
    private val usernameC = "usernameC"
    private val password = "password"

    // don't use mockBean (test Topic at same time)
    private val topicName1 = "topic1"
    private val topicName2 = "topic2"

    private val communityName1 = "communityName1"
    private val communityName2 = "communityName2"
    private val description = "description"

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

    private fun joinCommunity(body: String, communityId: Long, authentication: String?): ResultActionsDsl {
        return testHelper.post("/communities/$communityId/me/", body, authentication)
    }

    private fun joinCommunityRequest(role: String): String {
        return """
            {
                "role": "$role"
            }
        """.trimIndent()
    }

    private fun leaveCommunity(communityId: Long, authentication: String?): ResultActionsDsl {
        return testHelper.delete("/communities/$communityId/me/", null, authentication)
    }

    private fun modifyCommunity(communityId: Long, body: String, authentication: String?): ResultActionsDsl {
        return testHelper.put("/communities/$communityId/", body, authentication)
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
    }

    @Test
    @Order(1)
    fun `1_1_커뮤니티 생성_정상`() {
        // without login
        createCommunity(createCommunityRequest(communityName1, description, listOf("topic1", "topic2")), null)
            .andExpect {
                status { isUnauthorized() }
            }

        // with login
        val authenticationAdmin = testHelper.signinAndGetAuth("admin", password)
        createTopic(createTopicRequest("topic1"), authenticationAdmin)
            .andExpect {
                status { isCreated() }
            }
        createTopic(createTopicRequest("topic2"), authenticationAdmin)
            .andExpect {
                status { isCreated() }
            }

        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)
        createCommunity(
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2")),
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
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2")),
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
    fun `2_1_커뮤니티 구독_매니저_정상`() {
        /*val authentication1 = signinAndGetAuth(username1, password)
        createCommunity(
            authentication1,
            createCommunityRequest(communityName1, description, listOf("topic1", "topic2"))
        )
            .andExpect {
                status { isCreated() }
            }*/

        val authentication2 = testHelper.signinAndGetAuth(usernameB, password)
        joinCommunity(joinCommunityRequest("manager"), 1, authentication2)
            .andExpect {
                status { isCreated() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 2, 1))
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

        val authentication3 = testHelper.signinAndGetAuth(usernameC, password)
        joinCommunity(joinCommunityRequest("member"), 1, authentication3)
            .andExpect {
                status { isCreated() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 2, 2))
            }
    }

    @Test
    @Order(5)
    fun `2_3_커뮤니티 구독_해당 커뮤니티 없음`() {
        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)
        joinCommunity(joinCommunityRequest("member"), 2, authentication1)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 2, 3))
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
        val authentication2 = testHelper.signinAndGetAuth(usernameB, password)
        val authentication3 = testHelper.signinAndGetAuth(usernameC, password)
        // manager attempts rejoin as manager
        joinCommunity(joinCommunityRequest("manager"), 1, authentication2)
            .andExpect {
                status { isBadRequest() }
            }

        // manager attempts rejoin as member -> possible?
        joinCommunity(joinCommunityRequest("member"), 1, authentication2)
            .andExpect {
                status { isBadRequest() }
            }

        // member attempts rejoin as manager -> possible??
        joinCommunity(joinCommunityRequest("manager"), 1, authentication3)
            .andExpect {
                status { isBadRequest() }
            }

        // member attempts rejoin as member
        joinCommunity(joinCommunityRequest("member"), 1, authentication3)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 2, 4))
            }
    }

    @Test
    @Order(7)
    fun `3_1_커뮤니티 탈퇴_매니저_정상`() {
        val authentication2 = testHelper.signinAndGetAuth(usernameB, password)
        leaveCommunity(1, authentication2)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 3, 1))
            }
    }

    @Test
    @Order(8)
    fun `3_2_커뮤니티 탈퇴_일반 회원_정상`() {
        val authentication3 = testHelper.signinAndGetAuth(usernameC, password)
        leaveCommunity(1, authentication3)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 3, 2))
            }
    }

    @Test
    @Order(9)
    fun `3_3_커뮤니티 탈퇴_해당 커뮤니티 없음`() {
        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)
        leaveCommunity(2, authentication1)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 3, 3))
            }
    }

    @Test
    @Order(10)
    fun `3_4_커뮤니티 탈퇴_가입한 적 없음`() {
        testHelper.signup("username4", password)
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }
        val authentication4 = testHelper.signinAndGetAuth("username4", password)
        leaveCommunity(1, authentication4)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 3, 4))
            }
    }

    @Test
    @Order(11)
    fun `3_5_커뮤니티 탈퇴_이미 탈퇴함`() {
        val authentication2 = testHelper.signinAndGetAuth(usernameB, password)
        leaveCommunity(1, authentication2)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 3, 5))
            }
    }

    @Test
    @Order(12)
    fun `4_1_커뮤니티 정보 수정_정상`() {
        // TODO partial update

        val authenticationAdmin = testHelper.signinAndGetAuth("admin", password)
        createTopic(createTopicRequest("topic3"), authenticationAdmin)
        createTopic(createTopicRequest("topic4"), authenticationAdmin)

        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)

        val body = modifyCommunityRequest(
            "changedName1",
            "changedDescription",
            listOf("topic1", "topic3", "topic4")
        )

        modifyCommunity(1, body, authentication1)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 4, 1))
            }
    }

    @Test
    @Order(13)
    fun `4_2_커뮤니티 정보 수정_해당 커뮤니티 없음`() {
        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)
        val body = modifyCommunityRequest(
            "changedName2",
            "changedDescription",
            listOf("topic1")
        )
        modifyCommunity(2, body, authentication1)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 4, 2))
            }
    }

    @Test
    @Order(14)
    fun `4_3_커뮤니티 정보 수정_매니저 아님`() {
        // 현재 구독 중 x
        val authentication2 = testHelper.signinAndGetAuth(usernameB, password)
        val body = modifyCommunityRequest(
            "changedName2",
            "changedDescription2",
            listOf("topic1")
        )
        modifyCommunity(1, body, authentication2)
            .andExpect {
                status { isUnauthorized() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 4, 3))
            }

        // 일반 회원
        val authentication3 = testHelper.signinAndGetAuth(usernameC, password)
        joinCommunity(joinCommunityRequest("member"), 1, authentication3)
        val body2 = modifyCommunityRequest(
            "changedName2",
            "changedDescription2",
            listOf("topic1")
        )
        modifyCommunity(1, body2, authentication3)
            .andExpect {
                status { isUnauthorized() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 4, 3))
            }
    }

    @Test
    @Order(15)
    fun `4_4_커뮤니티 정보 수정_토픽 없음`() {
        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)
        val body = modifyCommunityRequest(
            "changedName2",
            "changedDescription2",
            listOf("topic10")
        )
        modifyCommunity(1, body, authentication1)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 4, 4))
            }
    }

    @Test
    @Order(18)
    fun `5_1_커뮤니티 삭제_정상`() {
        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)
        deleteCommunity(1, authentication1)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 5, 1))
            }
    }

    @Test
    @Order(17)
    fun `5_2_커뮤니티 삭제_커뮤니티 없음`() {
        val authentication1 = testHelper.signinAndGetAuth(usernameA, password)
        deleteCommunity(2, authentication1)
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 5, 2))
            }
    }

    @Test
    @Order(16)
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
