package wafflestudio.team4.reddit.global.oauth.info

import wafflestudio.team4.reddit.domain.user.model.User

abstract class OAuth2UserInfo(
    protected val attributes: Map<String, Any>
) {
    abstract val id: String
    abstract val name: String
    abstract val email: String
    abstract val imageUrl: String
}
