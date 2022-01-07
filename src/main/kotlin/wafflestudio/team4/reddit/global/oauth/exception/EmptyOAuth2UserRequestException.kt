package wafflestudio.team4.reddit.global.oauth.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.InvalidRequestException

class EmptyOAuth2UserRequestException(detail: String = "Empty OAuth2 Request") :
    InvalidRequestException(ErrorType.INVALID_REQUEST, detail)
