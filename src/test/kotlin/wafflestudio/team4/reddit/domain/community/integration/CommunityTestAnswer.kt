package wafflestudio.team4.reddit.domain.community.integration

import wafflestudio.team4.reddit.domain.community.dto.CommunityDto
import wafflestudio.team4.reddit.global.common.exception.ErrorResponse
import wafflestudio.team4.reddit.global.common.exception.ErrorType

object CommunityTestAnswer {
    private val admin = "admin"
    private val usernameA = "usernameA"
    private val usernameB = "usernameB"
    private val usernameC = "usernameC"
    private val password = "password"

    private val communityName1 = "communityName1"
    private val communityName2 = "communityName2"
    private val description = "description"
    private val changedDescription = "changedDescription"

    private val topicName1 = "topicName1"
    private val topicName2 = "topicName2"

    private val ans1 = arrayOf(
        // 1 1
        CommunityDto.Response(
            1,
            communityName1,
            0,
            1,
            description,
            false
        ),

        // 1 2
        ErrorResponse(
            ErrorType.COMMUNITY_ALREADY_EXISTS.code,
            ErrorType.COMMUNITY_ALREADY_EXISTS.name,
            "Community Already Exists",
        ),

    )

    private val ans2 = arrayOf(
        // 2 1
        CommunityDto.Response(
            1,
            communityName1,
            1,
            1,
            description,
            false
        ),
        // 2 2
        ErrorResponse(
            ErrorType.COMMUNITY_NOT_FOUND.code,
            ErrorType.COMMUNITY_NOT_FOUND.name,
            "Community Not Found",
        ),
        // 2 3
        ErrorResponse(
            ErrorType.INVALID_REQUEST.code,
            ErrorType.INVALID_REQUEST.name,
            "Already Joined",
        )
    )

    private val ans3 = arrayOf(
        // 3 1
        CommunityDto.Response(
            1,
            communityName1,
            1,
            1,
            changedDescription,
            false
        ),
        // 3 2
        CommunityDto.Response(
            1,
            communityName1,
            0,
            3,
            changedDescription,
            false
        ),
        // 3 3
        CommunityDto.Response(
            1,
            communityName1,
            0,
            2,
            changedDescription,
            false
        ),
        // 3 4
        CommunityDto.Response(
            1,
            communityName1,
            0,
            2,
            changedDescription,
            false
        ),
    )

    private val ans4 = arrayOf(
        // 4 1
        CommunityDto.Response(
            1,
            communityName1,
            0,
            2,
            changedDescription,
            false
        ),
        // 4 2
        ErrorResponse(
            ErrorType.COMMUNITY_NOT_FOUND.code,
            ErrorType.COMMUNITY_NOT_FOUND.name,
            "Community Not Found",
        ),
        // 4 3
        ErrorResponse(
            ErrorType.INVALID_REQUEST.code,
            ErrorType.INVALID_REQUEST.name,
            "Not Currently Joined"
        ),
        // 4 4
        ErrorResponse(
            ErrorType.INVALID_REQUEST.code,
            ErrorType.INVALID_REQUEST.name,
            "Not Currently Joined"
        )

    )

    private val ans5 = arrayOf(
        // 5 1
        CommunityDto.Response(
            1,
            communityName1,
            0,
            2,
            changedDescription,
            true
        ),
        // 5 2
        ErrorResponse(
            ErrorType.COMMUNITY_NOT_FOUND.code,
            ErrorType.COMMUNITY_NOT_FOUND.name,
            "Community Not Found",
        ),
        // 5 3
        ErrorResponse(
            ErrorType.UNAUTHORIZED.code,
            ErrorType.UNAUTHORIZED.name,
            "Not Community Manager"
        ),
    )

    val ans = arrayOf(ans1, ans2, ans3, ans4, ans5)
}
