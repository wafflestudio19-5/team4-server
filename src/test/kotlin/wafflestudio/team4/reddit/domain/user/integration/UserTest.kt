package wafflestudio.team4.reddit.domain.user.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
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
    // TODO db check
    private val username1 = "username1"
    private val username2 = "username2"
    private val password = "somepassword"

    private val testHelper = TestHelper(mockMvc, objectMapper)

    // set up
    @BeforeAll
    fun createUsers() {
        testHelper.signup(username1, password)
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }

        testHelper.signup(username2, password)
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }
    }

    @Test
    @Transactional
    fun `1_1_회원 가입_정상`() {
        testHelper.signup("username3", password)
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
        testHelper.signup(username1, password)
            .andExpect {
                status { isBadRequest() }
            }
            .andReturn()
            .let { assertTrue(testHelper.compare(it, 1, 2)) }
    }

    @Test
    @Transactional
    fun `2_1_로그인_정상`() {
        testHelper.signin(username1, password)
            .andExpect {
                status { isNoContent() }
                header { exists("Authentication") }
            }
    }

    @Test
    @Transactional
    fun `2_2_로그인_정보 오류`() {
        testHelper.signin(username1, "wrongPassword")
            .andExpect {
                status { isUnauthorized() }
            }

        testHelper.signin("wrongEmailFormat", password)
            .andExpect {
                status { isUnauthorized() }
            }

        testHelper.signin("", "")
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    @Transactional
    fun `3_1_GET_ME_정상`() {
        val url = "/users/me/"
        // without login
        testHelper.get(url, null)
            .andExpect {
                status { isUnauthorized() }
            }

        // with login
        testHelper.get(url, testHelper.signinAndGetAuth(username1, password))
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
        testHelper.get(url, null)
            .andExpect {
                status { isUnauthorized() }
            }

        // with login
        testHelper.get(url, testHelper.signinAndGetAuth(username2, password))
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { assertTrue(testHelper.compare(it, 3, 2)) }
    }

    @Test
    @Transactional
    fun `3_3_GET_USER_404`() {
        testHelper.get("/users/1234/", testHelper.signinAndGetAuth(username2, password))
            .andExpect {
                status { isNotFound() }
            }
            .andReturn()
            .let { assertTrue(testHelper.compare(it, 3, 3)) }
    }

    @Test
    @Transactional
    fun `4_1_UPDATE_USER_정상`() {
        val url = "/users/me/"
        val successFullBody =
            """
                {
                    "email": "updatename@snu.ac.kr",
                    "username": "updatename",
                    "password": "update_password"
                }
            """.trimIndent()

        // without login
        testHelper.put(url, successFullBody, null)
            .andExpect {
                status { isUnauthorized() }
            }

        // with login, full
        testHelper.put(url, successFullBody, testHelper.signinAndGetAuth(username2, password))
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { assertTrue(testHelper.compare(it, 4, 1)) }

        // password check
        testHelper.signin("updatename", "update_password")
            .andExpect {
                status { isNoContent() }
                header { exists("Authentication") }
            }
    }

    @Test
    @Transactional
    fun `4_2_UPDATE_USER_WITHOUT_EMAIL`() {
        val url = "/users/me/"
        val successBodyWithoutEmail =
            """
                {
                    "username": "updatename",
                    "password": "update_password"
                }
            """.trimIndent()
        testHelper.put(url, successBodyWithoutEmail, testHelper.signinAndGetAuth(username2, password))
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { assertTrue(testHelper.compare(it, 4, 2)) }

        // password check
        testHelper.signin(username2, "update_password")
            .andExpect {
                status { isNoContent() }
                header { exists("Authentication") }
            }
    }

    @Test
    @Transactional
    fun `4_3_UPDATE_USER_WITHOUT_NAME`() {
        val url = "/users/me/"
        val successBodyWithoutName =
            """
                {
                    "email": "updatename@snu.ac.kr",
                    "password": "update_password"
                }
            """.trimIndent()
        testHelper.put(url, successBodyWithoutName, testHelper.signinAndGetAuth(username2, password))
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { assertTrue(testHelper.compare(it, 4, 3)) }

        // password check
        testHelper.signin("updatename", "update_password")
            .andExpect {
                status { isNoContent() }
                header { exists("Authentication") }
            }
    }

    @Test
    @Transactional
    fun `4_4_UPDATE_USER_WITHOUT_PASSWORD`() {
        val url = "/users/me/"
        val successBodyWithoutPassword =
            """
                {
                    "email": "updatename@snu.ac.kr",
                    "username": "updatename"
                }
            """.trimIndent()
        testHelper.put(url, successBodyWithoutPassword, testHelper.signinAndGetAuth(username2, password))
            .andExpect {
                status { isOk() }
            }
            .andReturn()
            .let { assertTrue(testHelper.compare(it, 4, 4)) }
    }

    @Test
    @Transactional
    fun `4_5_UPDATE_USER_DUPLICATE_EMAIL`() {
        val url = "/users/me/"
        val duplicateEmailBody =
            """
                {
                    "email": "$username1@snu.ac.kr",
                    "username": "updatename"
                }
            """.trimIndent()
        testHelper.put(url, duplicateEmailBody, testHelper.signinAndGetAuth(username2, password))
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    @Transactional
    fun `5_1_DELETE_USER_성공`() {
        val url = "/users/me/"

        // without login
        testHelper.delete(url, null, null)
            .andExpect {
                status { isUnauthorized() }
            }

        // with login
        testHelper.delete(url, null, testHelper.signinAndGetAuth(username2, password))
            .andExpect {
                status { isNoContent() }
            }

        // login 시도
        testHelper.signin(username2, password)
            .andExpect {
                status { isUnauthorized() }
            }
    }
}
