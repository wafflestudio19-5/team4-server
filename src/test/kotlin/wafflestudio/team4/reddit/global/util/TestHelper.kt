package wafflestudio.team4.reddit.global.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import wafflestudio.team4.reddit.domain.user.integration.UserTestAnswer
import wafflestudio.team4.reddit.domain.community.integration.CommunityTestAnswer

class TestHelper(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) {
    fun signin(username: String, password: String): ResultActionsDsl {
        return mockMvc.post("/api/v1/users/signin/") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content =
                """
                    {
                        "email": "${toEmail(username)}",
                        "password": "$password"
                    }
                """.trimIndent()
        }
    }

    fun signup(body: String): ResultActionsDsl {
        return mockMvc.post("/api/v1/users/") {
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
        }
    }

    fun signinAndGetAuth(username: String, password: String): String {
        return signin(username, password)
            .andReturn()
            .response
            .getHeader("Authentication")!!
    }

    fun get(url: String, authentication: String?): ResultActionsDsl {
        val targetUrl = if (url.startsWith("/")) url else "/$url"
        return mockMvc.get("/api/v1$targetUrl") {
            if (authentication != null) {
                header("Authentication", authentication)
            }
        }
    }

    fun post(url: String, body: String, authentication: String?): ResultActionsDsl {
        val targetUrl = if (url.startsWith("/")) url else "/$url"
        return mockMvc.post("/api/v1$targetUrl") {
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
            if (authentication != null) {
                header("Authentication", authentication)
            }
        }
    }

    fun put(url: String, body: String, authentication: String?): ResultActionsDsl {
        val targetUrl = if (url.startsWith("/")) url else "/$url"
        return mockMvc.put("/api/v1$targetUrl") {
            content = (body)
            contentType = (MediaType.APPLICATION_JSON)
            accept = (MediaType.APPLICATION_JSON)
            if (authentication != null) {
                header("Authentication", authentication)
            }
        }
    }

    fun delete(url: String, body: String?, authentication: String?): ResultActionsDsl {
        val targetUrl = if (url.startsWith("/")) url else "/$url"
        return mockMvc.delete("/api/v1$targetUrl") {
            if (body != null) {
                content = (body)
                contentType = (MediaType.APPLICATION_JSON)
                accept = (MediaType.APPLICATION_JSON)
            }

            if (authentication != null) {
                header("Authentication", authentication)
            }
        }
    }

    fun toEmail(name: String): String {
        return "$name@snu.ac.kr"
    }

    fun dto2String(dtoObject: Any): String {
        return objectMapper.writeValueAsString(dtoObject)
    }

    fun jsonString2Map(jsonString: String): Map<String, String> {
        return objectMapper.readValue(jsonString)
    }

    fun compare(testResult: String, rightResult: String): Boolean {
        return compare(jsonString2Map(testResult), jsonString2Map(rightResult))
    }

    fun compare(testResult: Map<String, String>, rightResult: Map<String, String>): Boolean {
        val testResultWithoutLocalDateTime = HashMap(testResult)
        val rightResultWithoutLocalDateTime = HashMap(rightResult)
        testResultWithoutLocalDateTime.keys.removeIf { key ->
            key.contains("date")
        }
        rightResultWithoutLocalDateTime.keys.removeIf { key ->
            key.contains("date")
        }
        println("test result: $testResultWithoutLocalDateTime")
        println("answer     : $rightResultWithoutLocalDateTime")
        return testResultWithoutLocalDateTime == rightResultWithoutLocalDateTime
    }

    fun compare(mvcResult: MvcResult, testNum: Int, subTestNum: Int): Boolean {
        println("Compare called: $testNum $subTestNum")
        if (testNum < 1 || testNum > UserTestAnswer.ans.size) {
            println("Invalid test num")
            return false
        }
        if (subTestNum < 1 || subTestNum > UserTestAnswer.ans[testNum - 1].size) {
            println("Invalid sub test num")
            return false
        }
        val ansString = dto2String(UserTestAnswer.ans[testNum - 1][subTestNum - 1])
        val responseBody = mvcResult.response.contentAsString
        return compare(responseBody, ansString)
    }

    fun compareCommunity(mvcResult: MvcResult, testNum: Int, subTestNum: Int): Boolean {
        println("Compare called: $testNum $subTestNum")
        if (testNum < 1 || testNum > CommunityTestAnswer.ans.size) {
            println("Invalid test num")
            return false
        }
        if (subTestNum < 1 || subTestNum > CommunityTestAnswer.ans[testNum - 1].size) {
            println("Invalid sub test num")
            return false
        }
        val ansString = dto2String(CommunityTestAnswer.ans[testNum - 1][subTestNum - 1])
        val responseBody = mvcResult.response.contentAsString
        return compare(responseBody, ansString)
    }
}
