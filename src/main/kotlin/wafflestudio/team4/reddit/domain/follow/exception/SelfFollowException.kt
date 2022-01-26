package wafflestudio.team4.reddit.domain.follow.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.InvalidRequestException

class SelfFollowException(detail: String = "You cannot follow yourself") :
    InvalidRequestException(ErrorType.INVALID_REQUEST, detail)
