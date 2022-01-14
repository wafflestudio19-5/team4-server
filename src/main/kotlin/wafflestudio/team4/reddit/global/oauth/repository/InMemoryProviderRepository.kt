package wafflestudio.team4.reddit.global.oauth.repository

import wafflestudio.team4.reddit.global.oauth.provider.OAuthProvider

class InMemoryProviderRepository(
    private val providers: Map<String, OAuthProvider> // TODO copy
) {
    fun findByProviderName(name: String): OAuthProvider? {
        return providers[name]
    }
}
