package wafflestudio.team4.reddit.domain.user.integration

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class UserTest(private val mockMvc: MockMvc) {
    private val username1 = "username1"
    private val username2 = "username2"
    private val password = "somepassword"

    private fun toEmail(name: String): String {
        return "$name@snu.ac.kr"
    }

    private fun signin(email: String, password: String): ResultActionsDsl {
        return mockMvc.post("/api/v1/users/signin/") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content =
                """
                    {
                        "email": "$email",
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

    private fun signupRequest(username: String, password: String): String {
        return """
            {
                "email": "$username@snu.ac.kr",
                "username": "$username",
                "password": "$password",

            }
        """.trimIndent()
    }

    @BeforeEach
    fun `회원 가입`() {
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
    fun `회원 가입_정상`() {
        signup(signupRequest("username3", password))
            .andExpect {
                status { isCreated() }
                header { exists("Authentication") }
            }
    }

    @Test
    @Transactional
    fun `회원 가입_중복 이메일`() {
        signup(signupRequest(username1, password))
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    @Transactional
    fun `로그인_정상`() {
        signin(toEmail(username1), password)
            .andExpect {
                status { isOk() }
                header { exists("Authentication") }
            }
        // TODO response body checking
    }

    @Test
    @Transactional
    fun `로그인_정보 오류`() {
        signin(toEmail(username1), "wrongPassword")
            .andExpect {
                status { isUnauthorized() }
            }

        signin("wrongEmailFormat", password)
            .andExpect {
                status { isBadRequest() }
            }

        signin("", "")
            .andExpect {
                status { isBadRequest() }
            }
    }
}
