package wafflestudio.team4.reddit.domain.community.exception

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.InvalidRequestException

class CommunityAlreadyExistsException(detail: String = "Community Already Exists") :
    InvalidRequestException(ErrorType.COMMUNITY_ALREADY_EXISTS, detail)
