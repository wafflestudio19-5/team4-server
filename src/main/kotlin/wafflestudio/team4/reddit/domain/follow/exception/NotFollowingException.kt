package wafflestudio.team4.reddit.domain.follow.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.InvalidRequestException

class NotFollowingException(detail: String = "Not Following") :
    InvalidRequestException(ErrorType.INVALID_REQUEST, detail)
