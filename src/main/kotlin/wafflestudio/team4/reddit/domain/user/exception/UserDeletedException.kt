package wafflestudio.team4.reddit.domain.user.exception

import wafflestudio.team4.reddit.global.common.exception.DataNotFoundException
import wafflestudio.team4.reddit.global.common.exception.ErrorType

class UserDeletedException(detail: String = "User Deleted") :
    DataNotFoundException(ErrorType.USER_NOT_FOUND, detail)
