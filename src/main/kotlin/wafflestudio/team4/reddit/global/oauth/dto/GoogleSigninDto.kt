package wafflestudio.team4.reddit.global.oauth.dto

import javax.validation.constraints.NotNull

class GoogleSigninDto {
    data class TokenRequest(
        @field:NotNull
        val accessToken: String,
        @field:NotNull
        val refreshToken: String,
    )
}
