package wafflestudio.team4.reddit.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import wafflestudio.team4.reddit.domain.user.repository.UserRepository
import wafflestudio.team4.reddit.global.auth.annotation.MergedAuthenticationPrincipalArgumentResolver

@Configuration
class MergedAuthenticationPrincipalArgumentResolverConfigurer(
    private val userRepository: UserRepository,
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
//        super.addArgumentResolvers(resolvers)
//        println("resolver adding called")
        resolvers.add(0, MergedAuthenticationPrincipalArgumentResolver(userRepository))
    }
}
