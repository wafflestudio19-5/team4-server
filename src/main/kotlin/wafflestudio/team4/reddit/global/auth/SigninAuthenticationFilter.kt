package wafflestudio.team4.reddit.global.auth

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import wafflestudio.team4.reddit.global.auth.dto.LoginRequest
import wafflestudio.team4.reddit.global.auth.model.UserPrincipalDetailService
import java.io.BufferedReader
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SigninAuthenticationFilter(
    authenticationManager: AuthenticationManager?,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userPrincipalDetailService: UserPrincipalDetailService,
) : UsernamePasswordAuthenticationFilter(authenticationManager) {
    init {
        setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher("/api/v1/users/signin/", "POST"))
    }
    private var loginRequest: LoginRequest? = null

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        // TODO redirect to filter
//        val parsedRequest: LoginRequest = parseRequest(request)
        if (this.loginRequest == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return
        }
        // TODO multiple requests
        val isDeletedUser = userPrincipalDetailService.isDeletedUser(this.loginRequest!!)
        if (!isDeletedUser) {
            response.addHeader("Authentication", jwtTokenProvider.generateToken(authResult))
            response.status = HttpServletResponse.SC_NO_CONTENT
        } else {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
        }
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        super.unsuccessfulAuthentication(request, response, failed)
        response.status = HttpServletResponse.SC_UNAUTHORIZED
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        // Parse auth request
        val parsedRequest: LoginRequest = parseRequest(request)
        val authRequest: Authentication =
            UsernamePasswordAuthenticationToken(parsedRequest.email, parsedRequest.password)
        return authenticationManager.authenticate(authRequest)
    }

    private fun parseRequest(request: HttpServletRequest): LoginRequest {
        val reader: BufferedReader = request.reader
        val objectMapper = ObjectMapper()
        this.loginRequest = objectMapper.readValue(reader, LoginRequest::class.java)
        return this.loginRequest!!
    }
}
