package wafflestudio.team4.reddit.domain.comment.exception

import wafflestudio.team4.reddit.global.common.exception.DataNotFoundException
import wafflestudio.team4.reddit.global.common.exception.ErrorType

class CommentNotFoundException(detail: String = "Comment Not Found") :
    DataNotFoundException(ErrorType.COMMENT_NOT_FOUND, detail)
