package wafflestudio.team4.reddit.global.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.test.web.servlet.MvcResult
import wafflestudio.team4.reddit.domain.user.integration.UserTestAnswer

class TestHelper(
    private val objectMapper: ObjectMapper,
) {

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
}
