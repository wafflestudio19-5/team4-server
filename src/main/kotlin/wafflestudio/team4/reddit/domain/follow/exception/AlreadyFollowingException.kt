package wafflestudio.team4.reddit.domain.follow.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.InvalidRequestException

class AlreadyFollowingException(detail: String = "Already Following") :
    InvalidRequestException(ErrorType.INVALID_REQUEST, detail)
