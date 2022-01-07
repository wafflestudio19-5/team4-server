package wafflestudio.team4.reddit.domain.follow.integration

// import org.springframework.data.domain.Page
import wafflestudio.team4.reddit.domain.follow.dto.FollowDto
import wafflestudio.team4.reddit.global.common.dto.PageResponse
// import wafflestudio.team4.reddit.global.common.dto.PageResponse
import wafflestudio.team4.reddit.global.common.exception.ErrorResponse
import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.util.TestAnswer

object FollowTestAnswer : TestAnswer {
    private val ans1 = arrayOf(
        FollowDto.Response(
            id = 3,
            fromUserName = "usernameY",
            toUserName = "usernameZ",
            deleted = false
        ),

        // dummy
        ErrorResponse(
            ErrorType.INVALID_REQUEST.code,
            ErrorType.INVALID_REQUEST.name,
            "Invalid Request"
        )
    )

    private val ans2 = arrayOf(
        FollowDto.Response(
            id = 3,
            fromUserName = "usernameY",
            toUserName = "usernameZ",
            deleted = true
        ),

        // dummy
        ErrorResponse(
            ErrorType.INVALID_REQUEST.code,
            ErrorType.INVALID_REQUEST.name,
            "Invalid Request"
        )
    )

    private val ans3 = arrayOf(
        PageResponse(
            content = listOf(
                FollowDto.FollowerResponse(
                    id = 3,
                    username = "usernameZ",
                    email = "usernameZ@snu.ac.kr"
                ),
                FollowDto.FollowerResponse(
                    id = 1,
                    username = "usernameX",
                    email = "usernameX@snu.ac.kr"
                ),
            ),
            size = 10,
            numberOfElements = 2,
            links = null,
        ),

        // dummy
        ErrorResponse(
            ErrorType.INVALID_REQUEST.code,
            ErrorType.INVALID_REQUEST.name,
            "Invalid Request"
        )
    )

    override val ans = arrayOf(ans1, ans2, ans3)
}
