package wafflestudio.team4.reddit.global.oauth.provider

import wafflestudio.team4.reddit.global.oauth.property.OAuthProperties

data class OAuthProvider(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val tokenUri: String,
    val userInfoUri: String,
) {
    constructor(user: OAuthProperties.User, provider: OAuthProperties.Provider) : this(
        user.clientId,
        user.clientSecret,
        user.redirectUri,
        provider.tokenUri,
        provider.userInfoUri,
    )
}
