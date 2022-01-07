package wafflestudio.team4.reddit.domain.user.exception

import wafflestudio.team4.reddit.global.common.exception.DataNotFoundException
import wafflestudio.team4.reddit.global.common.exception.ErrorType

class UserProfileNotFoundException(detail: String = "User profile not found") :
    DataNotFoundException(ErrorType.PROFILE_NOT_FOUND, detail)
