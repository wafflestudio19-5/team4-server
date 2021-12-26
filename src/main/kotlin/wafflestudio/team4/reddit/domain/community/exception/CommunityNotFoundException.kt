package wafflestudio.team4.reddit.domain.community.exception

import wafflestudio.team4.reddit.global.common.exception.DataNotFoundException
import wafflestudio.team4.reddit.global.common.exception.ErrorType

class CommunityNotFoundException(detail: String = "Community Not Found") :
    DataNotFoundException(ErrorType.COMMUNITY_NOT_FOUND, detail)
