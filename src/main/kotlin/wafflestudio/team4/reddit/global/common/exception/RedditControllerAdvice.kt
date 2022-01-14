package wafflestudio.team4.reddit.global.common.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RedditControllerAdvice {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    @ExceptionHandler(value = [DataNotFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun notfound(e: RedditException): ErrorResponse {
        logger.error(e.errorType.name + " " + e.detail)
        return ErrorResponse(e.errorType.code, e.errorType.name, e.detail)
    }

    @ExceptionHandler(value = [InvalidRequestException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun badRequest(e: RedditException): ErrorResponse {
        logger.error(e.errorType.name + " " + e.detail)
        return ErrorResponse(e.errorType.code, e.errorType.name, e.detail)
    }

    @ExceptionHandler(value = [NotAllowedException::class])
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun notAllowed(e: RedditException): ErrorResponse {
        logger.error(e.errorType.name + " " + e.detail)
        return ErrorResponse(e.errorType.code, e.errorType.name, e.detail)
    }

    @ExceptionHandler(value = [ConflictException::class])
    @ResponseStatus(HttpStatus.CONFLICT)
    fun conflict(e: RedditException): ErrorResponse {
        logger.error(e.errorType.name + " " + e.detail)
        return ErrorResponse(e.errorType.code, e.errorType.name, e.detail)
    }

    @ExceptionHandler(value = [UnauthorizedException::class])
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun unauthorized(e: RedditException): ErrorResponse {
        logger.error(e.errorType.name + " " + e.detail)
        return ErrorResponse(e.errorType.code, e.errorType.name, e.detail)
    }

    @ExceptionHandler(value = [InternalServerException::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun serverError(e: RedditException): ErrorResponse {
        logger.error(e.errorType.name + " " + e.detail)
        return ErrorResponse(e.errorType.code, e.errorType.name, e.detail)
    }

    // DTO Validation
    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun dtoValidationFailed(e: MethodArgumentNotValidException): ErrorResponse {
        val bindingResult = e.bindingResult
        val stringBuilder = StringBuilder()

        bindingResult.fieldErrors.forEach {
            fieldError ->
            stringBuilder
                .append(fieldError.field)
                .append(": ")
                .append(fieldError.defaultMessage)
                .append(", ")
        }
        val errorMessage = stringBuilder.toString()
        logger.error(ErrorType.VALIDATION_FAILED.name + " " + errorMessage)
        return ErrorResponse(ErrorType.VALIDATION_FAILED.code, ErrorType.VALIDATION_FAILED.name, errorMessage)
    }

    // TODO jwt expiration
}
