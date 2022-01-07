package wafflestudio.team4.reddit.domain.comment.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.UnauthorizedException

class NotCommentOwnerException(detail: String = "Not Comment Owner") :
    UnauthorizedException(ErrorType.UNAUTHORIZED, detail)
