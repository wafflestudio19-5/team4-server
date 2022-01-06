package wafflestudio.team4.reddit.global.oauth.info.impl

import wafflestudio.team4.reddit.global.oauth.exception.EmptyOAuth2UserAttributeException
import wafflestudio.team4.reddit.global.oauth.info.OAuth2UserInfo

class GoogleOAuth2UserInfo(
    attributes: Map<String, Any>,
) : OAuth2UserInfo(attributes) {
    override fun getId(): String {
        return attributes["sub"] as? String ?: throw EmptyOAuth2UserAttributeException()
    }

    override fun getName(): String {
        return attributes["name"] as? String ?: throw EmptyOAuth2UserAttributeException()
    }

    override fun getEmail(): String {
        return attributes["email"] as? String ?: throw EmptyOAuth2UserAttributeException()
    }

    override fun getImageUrl(): String {
        return attributes["picture"] as? String ?: throw EmptyOAuth2UserAttributeException()
    }
}
