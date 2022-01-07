package wafflestudio.team4.reddit.global.oauth.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.InternalServerException

class InvalidOAuthProviderException(detail: String = "Failed to load provider")
    : InternalServerException(ErrorType.INTERNAL_ERROR, detail)
