package wafflestudio.team4.reddit.domain.user.integration

import wafflestudio.team4.reddit.domain.user.dto.UserDto
import wafflestudio.team4.reddit.global.common.exception.ErrorResponse
import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.util.TestAnswer

object UserTestAnswer : TestAnswer {
    private val username1 = "username1"
    private val username2 = "username2"
    private val username3 = "username3"
    private val updateUsername = "updatename"

    private val ans1 = arrayOf(
        // 1 1
        UserDto.Response(
            3,
            username3,
            "$username3@snu.ac.kr",
            null,
        ),

        // 1 2
        ErrorResponse(
            ErrorType.USER_ALREADY_EXISTS.code,
            ErrorType.USER_ALREADY_EXISTS.name,
            "User already exists",
        ),
    )

    // test 2: body non-required
    private val ans2: Array<Any> = arrayOf("")

    private val ans3 = arrayOf(
        // 3 1
        UserDto.Response(
            1,
            username1,
            "$username1@snu.ac.kr",
            null,
        ),

        // 3 2
        UserDto.Response(
            2,
            username2,
            "$username2@snu.ac.kr",
            null,
        ),

        // 3 3: 404
        ErrorResponse(
            ErrorType.USER_NOT_FOUND.code,
            ErrorType.USER_NOT_FOUND.name,
            "User not found",
        )
    )

    private val ans4 = arrayOf(
        // 4 1
        UserDto.Response(
            2,
            updateUsername,
            "$updateUsername@snu.ac.kr",
            null,
        ),

        // 4 2
        UserDto.Response(
            2,
            updateUsername,
            "$username2@snu.ac.kr",
            null,
        ),

        // 4 3
        UserDto.Response(
            2,
            username2,
            "$updateUsername@snu.ac.kr",
            null,
        ),

        // 4 4
        UserDto.Response(
            2,
            updateUsername,
            "$updateUsername@snu.ac.kr",
            null,
        ),

        // 4 5
        ErrorResponse(
            ErrorType.USER_ALREADY_EXISTS.code,
            ErrorType.USER_ALREADY_EXISTS.name,
            "User already exists",
        )
    )

    override val ans = arrayOf(ans1, ans2, ans3, ans4)
}
