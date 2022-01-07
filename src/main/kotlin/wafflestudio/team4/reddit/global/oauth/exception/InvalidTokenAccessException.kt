package wafflestudio.team4.reddit.global.oauth.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.InternalServerException

class InvalidTokenAccessException(detail: String = "Failed to access token") :
    InternalServerException(ErrorType.INVALID_REQUEST, detail)
