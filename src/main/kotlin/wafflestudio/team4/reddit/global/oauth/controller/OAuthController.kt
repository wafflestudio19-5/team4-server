package wafflestudio.team4.reddit.global.oauth.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wafflestudio.team4.reddit.domain.user.dto.UserDto
import wafflestudio.team4.reddit.global.auth.jwt.JwtTokenProvider
import wafflestudio.team4.reddit.global.oauth.dto.GoogleSigninDto
import wafflestudio.team4.reddit.global.oauth.service.OAuthService
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/social_login")
class OAuthController(
    private val oAuthService: OAuthService,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    // TODO real oauth2
//    @GetMapping("/signin/oauth/{provider}")
//    fun signin(
//        @PathVariable provider: String,
//        @RequestParam code: String,
//    ): ResponseEntity<UserDto.Response> {
//        val user = oAuthService.signin(provider, code)
//        val headers = HttpHeaders()
//        headers.set("Authentication", jwtTokenProvider.generateToken(user.email)) // TODO refresh token
//        return ResponseEntity(UserDto.Response(user), headers, HttpStatus.OK)
//    }

    @PostMapping("/{provider}/")
    fun getUserAccessToken(
        @Valid @RequestBody tokenRequest: GoogleSigninDto.TokenRequest,
        @PathVariable provider: String
    ): ResponseEntity<UserDto.Response> {
        val user = oAuthService.signinWithToken(provider, tokenRequest.accessToken)
        val headers = HttpHeaders()
        headers.set("Authentication", jwtTokenProvider.generateToken(user.email)) // TODO refresh token
        return ResponseEntity(UserDto.Response(user), headers, HttpStatus.OK)
    }
}
