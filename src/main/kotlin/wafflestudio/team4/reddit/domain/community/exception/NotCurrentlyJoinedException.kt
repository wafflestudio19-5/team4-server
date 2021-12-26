package wafflestudio.team4.reddit.domain.community.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.InvalidRequestException

class NotCurrentlyJoinedException(detail: String = "Not Currently Joined") :
    InvalidRequestException(ErrorType.INVALID_REQUEST, detail)
