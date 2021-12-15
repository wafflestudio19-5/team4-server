package wafflestudio.team4.reddit.domain.user.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.InvalidRequestException

class UserAlreadyExistsException(detail: String = "User already exists") :
    InvalidRequestException(ErrorType.USER_ALREADY_EXISTS, detail)
