package wafflestudio.team4.reddit.global.oauth.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.InvalidRequestException

class EmptyOAuth2UserAttributeException(detail: String = "Empty attributes for this provider")
    : InvalidRequestException(ErrorType.INVALID_REQUEST, detail)
