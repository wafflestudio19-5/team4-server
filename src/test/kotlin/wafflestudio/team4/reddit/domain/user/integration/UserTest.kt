package wafflestudio.team4.reddit.domain.user.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import wafflestudio.team4.reddit.global.util.TestHelper

@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class UserTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) {
    private val username1 = "username1"
    private val username2 = "username2"
    private val password = "somepassword"

    private val testHelper = TestHelper(objectMapper)

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

    private fun get(url: String, authentication: String?): ResultActionsDsl {
        val targetUrl = if (url.startsWith("/")) url else "/$url"
        return mockMvc.get("/api/v1$targetUrl") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
        }
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

    // set up

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
    }

    @Test
    @Transactional
    fun `1_1_회원 가입_정상`() {
        signup(signupRequest("username3", password))
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }
            .andReturn()
            .let { mvcResult ->
                assertTrue(testHelper.compare(mvcResult, 1, 1))
            }
    }

    @Test
    @Transactional
    fun `1_2_회원 가입_중복 이메일`() {
        signup(signupRequest(username1, password))
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { assertTrue(testHelper.compare(it, 1, 2)) }
    }

    @Test
    @Transactional
    fun `2_1_로그인_정상`() {
        signin(username1, password)
            .andExpect {
                status { isNoContent() }
                header { exists("Authentication") }
            }
    }

    @Test
    @Transactional
    fun `2_2_로그인_정보 오류`() {
        signin(username1, "wrongPassword")
            .andExpect {
                status { isUnauthorized() }
            }

        signin("wrongEmailFormat", password)
            .andExpect {
                status { isUnauthorized() }
            }

        signin("", "")
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    @Transactional
    fun `3_1_GET_ME_정상`() {
        val url = "/users/me/"
        // without login
        get(url, null)
            .andExpect {
                status { isUnauthorized() }
            }

        // with login
        get(url, signinAndGetAuth(username1, password))
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { assertTrue(testHelper.compare(it, 3, 1)) }
    }

    @Test
    @Transactional
    fun `3_2_GET_USER_정상`() {
        val url = "/users/2/"

        // without login
        get(url, null)
            .andExpect {
                status { isUnauthorized() }
            }

        // with login
        get(url, signinAndGetAuth(username2, password))
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { assertTrue(testHelper.compare(it, 3, 2)) }
    }

    @Test
    @Transactional
    fun `3_3_GET_USER_404`() {
        get("/users/1234/", signinAndGetAuth(username2, password))
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { assertTrue(testHelper.compare(it, 3, 3)) }
    }
}
