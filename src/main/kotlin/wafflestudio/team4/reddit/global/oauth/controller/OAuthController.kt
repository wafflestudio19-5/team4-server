package wafflestudio.team4.reddit.global.oauth.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import wafflestudio.team4.reddit.domain.user.dto.UserDto
import wafflestudio.team4.reddit.global.auth.jwt.JwtTokenProvider
import wafflestudio.team4.reddit.global.oauth.service.OAuthService

@RestController
@RequestMapping("/api/v1/social_login")
class OAuthController(
    private val oAuthService: OAuthService,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    // TODO real oauth2
    @GetMapping("/signin/oauth/{provider}")
    fun signin(
        @PathVariable provider: String,
        @RequestParam code: String,
    ): ResponseEntity<UserDto.Response> {
        val user = oAuthService.signin(provider, code)
        val headers = HttpHeaders()
        headers.set("Authentication", jwtTokenProvider.generateToken(user.email)) // TODO refresh token
        return ResponseEntity(UserDto.Response(user), headers, HttpStatus.OK)
    }

    @PostMapping("/google/")
    fun socialLogin() {
    }
}
