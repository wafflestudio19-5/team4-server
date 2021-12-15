package wafflestudio.team4.reddit.global.common.exception

enum class ErrorType(
    val code: Int
) {
    // Error code rule:
    // (status)|(case)
    // ex. default 404 : 4040
    //     404 for user: 4041
    //     404 for post: 4042 ...

    INVALID_REQUEST(4000),
    USER_ALREADY_EXISTS(4001),
    VALIDATION_FAILED(4002),

    UNAUTHORIZED(4010),
    USER_WRONG_EMAIL_PASSWORD(4011),

    NOT_FOUND(4040),
    USER_NOT_FOUND(4041),
}
