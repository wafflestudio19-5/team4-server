package wafflestudio.team4.reddit.domain.user.exception

import wafflestudio.team4.reddit.global.common.exception.DataNotFoundException
import wafflestudio.team4.reddit.global.common.exception.ErrorType

class UserImageNotFoundException(detail: String = "User image not found") :
    DataNotFoundException(ErrorType.IMAGE_NOT_FOUND, detail)
