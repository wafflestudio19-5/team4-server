package wafflestudio.team4.reddit.domain.topic.exceptions

import wafflestudio.team4.reddit.global.common.exception.ErrorType
import wafflestudio.team4.reddit.global.common.exception.InvalidRequestException

class TopicAlreadyExistsException(detail: String = "Topic Already Exists") :
    InvalidRequestException(ErrorType.TOPIC_ALREADY_EXISTS, detail)
