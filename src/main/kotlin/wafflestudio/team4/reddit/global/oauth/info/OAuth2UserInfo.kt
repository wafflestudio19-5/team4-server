package wafflestudio.team4.reddit.global.oauth.info

import wafflestudio.team4.reddit.domain.user.model.User

abstract class OAuth2UserInfo(
    protected val attributes: Map<String, Any>
) {
    abstract fun getId(): String
    abstract fun getName(): String
    abstract fun getEmail(): String
    abstract fun getImageUrl(): String

    fun asUser(): User {
    }
}
