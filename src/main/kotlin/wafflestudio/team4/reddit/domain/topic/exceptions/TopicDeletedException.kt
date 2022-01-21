package wafflestudio.team4.reddit.domain.topic.exceptions

import wafflestudio.team4.reddit.global.common.exception.DataNotFoundException
import wafflestudio.team4.reddit.global.common.exception.ErrorType

class TopicDeletedException(detail: String = "Topic Deleted") :
    DataNotFoundException(ErrorType.NOT_FOUND, detail)
