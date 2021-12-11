package wafflestudio.team4.reddit.domain.user.exception

import wafflestudio.team4.reddit.global.common.exception.ConflictException
import wafflestudio.team4.reddit.global.common.exception.ErrorType

class UserAlreadyExistsException(detail: String = "User already exists") :
    ConflictException(ErrorType.INVALID_REQUEST, detail)
// TODO: Error Type
