package wafflestudio.team4.reddit.domain.user.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.UnauthorizedException

class UnauthorizedSigninException(detail: String = "Email or password is wrong") :
    UnauthorizedException(ErrorType.USER_WRONG_EMAIL_PASSWORD, detail)
