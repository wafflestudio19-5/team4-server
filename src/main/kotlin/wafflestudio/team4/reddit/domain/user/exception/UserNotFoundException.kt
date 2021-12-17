package wafflestudio.team4.reddit.domain.user.exception

import wafflestudio.team4.reddit.global.common.exception.DataNotFoundException
import wafflestudio.team4.reddit.global.common.exception.ErrorType

class UserNotFoundException(detail: String = "User not found") :
    DataNotFoundException(ErrorType.USER_NOT_FOUND, detail)
