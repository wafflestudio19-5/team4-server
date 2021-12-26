package wafflestudio.team4.reddit.domain.topic.exceptions

import wafflestudio.team4.reddit.global.common.exception.DataNotFoundException
import wafflestudio.team4.reddit.global.common.exception.ErrorType

class TopicNotFoundException(detail: String = "Topic Not Found") :
    DataNotFoundException(ErrorType.NOT_FOUND, detail)
