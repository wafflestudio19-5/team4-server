package wafflestudio.team4.reddit.domain.community.exception

import wafflestudio.team4.reddit.global.common.exception.DataNotFoundException
import wafflestudio.team4.reddit.global.common.exception.ErrorType

class CommunityDeletedException(detail: String = "Community Deleted") :
    DataNotFoundException(ErrorType.NOT_FOUND, detail)
