package wafflestudio.team4.reddit.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import wafflestudio.team4.reddit.global.auth.jwt.JwtAuthenticationEntryPoint
import wafflestudio.team4.reddit.global.auth.filter.JwtAuthenticationFilter
import wafflestudio.team4.reddit.global.auth.jwt.JwtTokenProvider
import wafflestudio.team4.reddit.global.auth.filter.SigninAuthenticationFilter
import wafflestudio.team4.reddit.global.oauth.service.OAuthUserService
import wafflestudio.team4.reddit.global.auth.service.UserPrincipalDetailService

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userPrincipalDetailService: UserPrincipalDetailService,
    private val oAuthUserService: OAuthUserService,
) : WebSecurityConfigurerAdapter() {
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(daoAuthenticationProvider())
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
//       return Argon2PasswordEncoder()
        return BCryptPasswordEncoder()
    }

    @Bean
    fun daoAuthenticationProvider(): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setPasswordEncoder(passwordEncoder())
        provider.setUserDetailsService(userPrincipalDetailService)
        return provider
    }

    override fun configure(http: HttpSecurity) {
//        userPrincipalDetailService.passwordEncoder = passwordEncoder()
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
            .addFilter(
                SigninAuthenticationFilter(authenticationManager(), jwtTokenProvider, userPrincipalDetailService)
//              SigninAuthenticationFilter(authenticationManager(), jwtTokenProvider)
            )
            .addFilter(JwtAuthenticationFilter(authenticationManager(), jwtTokenProvider))
            .authorizeRequests()
            .antMatchers("/api/v1/users/signin/").permitAll() // Auth entrypoint
            .antMatchers(HttpMethod.POST, "/api/v1/users/").anonymous() // SignUp user
            .antMatchers("/api/v1/ping/").permitAll()
            .antMatchers("/profile").permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
            .userInfoEndpoint()
            .userService(oAuthUserService)
    }
}
