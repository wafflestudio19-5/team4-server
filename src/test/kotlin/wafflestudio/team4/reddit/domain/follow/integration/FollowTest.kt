package wafflestudio.team4.reddit.domain.follow.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Order

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActionsDsl
import wafflestudio.team4.reddit.global.util.TestHelper

// @Transactional
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class FollowTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {
    private val testHelper = TestHelper(mockMvc, objectMapper)

    private val usernameX = "usernameX"
    private val usernameY = "usernameY"
    private val usernameZ = "usernameZ"
    private val password = "password"

    private fun follow(toUserId: Long, authentication: String?): ResultActionsDsl {
        return testHelper.post("/follow/$toUserId/", null, authentication)
    }

    private fun unfollow(toUserId: Long, authentication: String?): ResultActionsDsl {
        return testHelper.delete("/follow/$toUserId/", null, authentication)
    }

    private fun getFollowersPage(
        toUserId: Long,
        lastFollowId: Long,
        size: Int,
        authentication: String?
    ): ResultActionsDsl {
        return testHelper.get(
            "/follow/$toUserId/followers/?lastFollowId=$lastFollowId&size=$size",
            authentication
        )
    }

    private fun compare(mvcResult: MvcResult, testNum: Int, subTestNum: Int): Boolean {
        return testHelper.compare(FollowTestAnswer, mvcResult, testNum, subTestNum)
    }

    @BeforeAll
    fun createUsers() {
        testHelper.signup(usernameX, password)
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        testHelper.signup(usernameY, password)
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        testHelper.signup(usernameZ, password)
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }
    }

    @Test
    @Order(1)
    fun `1_1_팔로우_정상`() {
        // 1 -> 2
        val authentication1 = testHelper.signinAndGetAuth(usernameX, password)
        follow(2, authentication1)
            .andExpect {
                status { isCreated() }
            }

        // 3 -> 2
        val authentication3 = testHelper.signinAndGetAuth(usernameZ, password)
        follow(2, authentication3)
            .andExpect {
                status { isCreated() }
            }

        // 2 -> 3
        val authentication2 = testHelper.signinAndGetAuth(usernameY, password)
        follow(3, authentication2)
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
    fun `2_1_언팔_정상`() {
        // 2 -> 3
        val authentication2 = testHelper.signinAndGetAuth(usernameY, password)
        unfollow(3, authentication2)
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 2, 1))
            }
    }

    @Test
    @Order(3)
    fun `3_1_GET_FOLLOWERS_정상`() {
        // 2
        val authentication3 = testHelper.signinAndGetAuth(usernameZ, password)
        getFollowersPage(2, 100, 10, authentication3)
            .andExpect {
                status { isOk() }
            }
            /*.andReturn()
            .let{ mvcResult ->
                Assertions.assertTrue(compare(mvcResult, 3, 1))
            }*/
    }
}
