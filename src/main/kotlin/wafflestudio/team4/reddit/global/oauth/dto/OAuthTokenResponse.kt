package wafflestudio.team4.reddit.global.oauth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class OAuthTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    val scope: String,
    @JsonProperty("token_type")
    val tokenType: String,
)
