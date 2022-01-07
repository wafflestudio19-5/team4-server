package wafflestudio.team4.reddit.global.oauth.info.impl

import wafflestudio.team4.reddit.global.oauth.exception.EmptyOAuth2UserAttributeException
import wafflestudio.team4.reddit.global.oauth.info.OAuth2UserInfo

class GoogleOAuth2UserInfo(
    attributes: Map<String, Any>,
) : OAuth2UserInfo(attributes) {
    override val id: String
        get() = attributes["sub"] as? String ?: throw EmptyOAuth2UserAttributeException()

    override val name: String
        get() = attributes["name"] as? String ?: throw EmptyOAuth2UserAttributeException()

    override val email: String
        get() = attributes["email"] as? String ?: throw EmptyOAuth2UserAttributeException()

    override val imageUrl: String
        get() = attributes["picture"] as? String ?: throw EmptyOAuth2UserAttributeException()
}
