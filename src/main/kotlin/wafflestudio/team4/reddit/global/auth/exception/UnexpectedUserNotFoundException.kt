package wafflestudio.team4.reddit.global.auth.exception

import wafflestudio.team4.reddit.global.common.exception.DataNotFoundException
import wafflestudio.team4.reddit.global.common.exception.ErrorType

class UnexpectedUserNotFoundException(detail: String = "User should exist, but unexpectedly not found") :
    DataNotFoundException(ErrorType.USER_NOT_FOUND, detail)
