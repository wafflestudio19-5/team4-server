package wafflestudio.team4.reddit.global.oauth.info

import wafflestudio.team4.reddit.global.oauth.info.impl.GoogleOAuth2UserInfo

class OAuth2UserInfoFactory private constructor() {
    companion object Factory {
        fun extractFrom(
            providerType: ProviderType,
            attributes: Map<String, Any>
        ): OAuth2UserInfo {
            when (providerType) {
                ProviderType.GOOGLE -> return GoogleOAuth2UserInfo(attributes)
            }
        }
    }
}
