package wafflestudio.team4.reddit.domain.post.exception

import wafflestudio.team4.reddit.global.common.exception.DataNotFoundException
import wafflestudio.team4.reddit.global.common.exception.ErrorType

class PostNotFoundException(detail: String = "Post Not Found") :
    DataNotFoundException(ErrorType.POST_NOT_FOUND, detail)
