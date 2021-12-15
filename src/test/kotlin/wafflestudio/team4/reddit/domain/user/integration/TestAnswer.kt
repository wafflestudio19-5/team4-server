package wafflestudio.team4.reddit.domain.user.integration

import wafflestudio.team4.reddit.domain.user.dto.UserDto
import wafflestudio.team4.reddit.global.common.exception.ErrorResponse
import wafflestudio.team4.reddit.global.common.exception.ErrorType

object UserTestAnswer {
    val ans_1_1 =
        UserDto.Response(
            3,
            "username3",
            "username3@snu.ac.kr",
            null,
        )

    val ans_1_2 =
        ErrorResponse(
            ErrorType.USER_ALREADY_EXISTS.code,
            ErrorType.USER_ALREADY_EXISTS.name,
            "User already exists",
        )

    private val ans1 = arrayOf(ans_1_1, ans_1_2)

    val ans = arrayOf(ans1)
}

object PostTestAnswer
