package wafflestudio.team4.reddit.global.oauth.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.InternalServerException

class InvalidUserInfoAccessException(detail: String = "Failed to access user info") :
    InternalServerException(ErrorType.INTERNAL_ERROR, detail)
