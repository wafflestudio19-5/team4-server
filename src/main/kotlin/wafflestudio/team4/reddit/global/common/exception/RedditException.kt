package wafflestudio.team4.reddit.global.common.exception

import java.lang.RuntimeException

abstract class RedditException(val errorType: ErrorType, val detail: String = "") : RuntimeException(errorType.name)

abstract class InvalidRequestException(errorType: ErrorType, detail: String = "") : RedditException(errorType, detail)
abstract class DataNotFoundException(errorType: ErrorType, detail: String = "") : RedditException(errorType, detail)
abstract class NotAllowedException(errorType: ErrorType, detail: String = "") : RedditException(errorType, detail)
abstract class ConflictException(errorType: ErrorType, detail: String = "") : RedditException(errorType, detail)