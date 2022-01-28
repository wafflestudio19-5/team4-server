package wafflestudio.team4.reddit.global.auth.annotation

import org.springframework.core.MethodParameter
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import wafflestudio.team4.reddit.domain.user.exception.UnauthorizedSigninException
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.repository.UserRepository
import wafflestudio.team4.reddit.global.util.auth.AnnotationUtil
import java.lang.ClassCastException

@Component
class MergedAuthenticationPrincipalArgumentResolver(
    private val userRepository: UserRepository,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return AnnotationUtil.findMethodAnnotation(AuthenticationPrincipal::class.java, parameter) != null
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication ?: return null

        val user: User = authentication.principal as? User ?: throw UnauthorizedSigninException()

        val authPrincipal: AuthenticationPrincipal? =
            AnnotationUtil.findMethodAnnotation(AuthenticationPrincipal::class.java, parameter)

        if (!parameter.parameterType.isAssignableFrom(user.javaClass)) {
            throw ClassCastException(user.username + " is not assignable to " + parameter.parameterType)
        }
        return userRepository.findByIdOrNull(user.id) ?: throw UnauthorizedSigninException()
    }
}
