package wafflestudio.team4.reddit.domain.community.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.UnauthorizedException

class NotCommunityManagerException(detail: String = "Not Community Manager") :
    UnauthorizedException(ErrorType.UNAUTHORIZED, detail)
