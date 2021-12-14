package wafflestudio.team4.reddit.global.auth

import org.springframework.security.core.annotation.AuthenticationPrincipal

@Target(AnnotationTarget.VALUE_PARAMETER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal(expression = "user")
annotation class CurrentUser(val require: Boolean = true)
