package wafflestudio.team4.reddit.domain.post.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.UnauthorizedException

class NotPostOwnerException(detail: String = "Not Post Owner") :
    UnauthorizedException(ErrorType.UNAUTHORIZED, detail)
