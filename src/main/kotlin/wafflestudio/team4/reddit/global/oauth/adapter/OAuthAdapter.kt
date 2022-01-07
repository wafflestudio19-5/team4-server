package wafflestudio.team4.reddit.global.oauth.adapter

import wafflestudio.team4.reddit.global.oauth.exception.InvalidOAuthProviderException
import wafflestudio.team4.reddit.global.oauth.property.OAuthProperties
import wafflestudio.team4.reddit.global.oauth.provider.OAuthProvider

class OAuthAdapter private constructor() {
    companion object {
        fun getOAuthProviders(oAuthProperties: OAuthProperties): Map<String, OAuthProvider> {
            val oAuthProvider = HashMap<String, OAuthProvider>()

            oAuthProperties.user.forEach {
                (k, v) ->
                oAuthProvider[k] =
                    OAuthProvider(v, oAuthProperties.provider[k] ?: throw InvalidOAuthProviderException())
            }
            return oAuthProvider
        }
    }
}
