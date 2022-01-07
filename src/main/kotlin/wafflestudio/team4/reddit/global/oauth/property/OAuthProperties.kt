package wafflestudio.team4.reddit.global.oauth.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

// @ConfigurationProperties(prefix = "oauth2")
// class OAuthProperties {
//    val user = HashMap<String, User>()
//    val provider = HashMap<String, Provider>()
//
//    data class User(
//        val clientId: String,
//        val clientSecret: String,
//        val redirectUri: String,
//    )
//
//    data class Provider(
//        val tokenUri: String,
//        val userInfoUri: String,
//        val userNameAttribute: String,
//    )
// }

@ConstructorBinding
@ConfigurationProperties(prefix = "oauth2")
class OAuthProperties(
    val user: Map<String, User> = HashMap(),
    val provider: Map<String, Provider> = HashMap(),
) {
    data class User(
        val clientId: String,
        val clientSecret: String,
        val redirectUri: String,
    )

    data class Provider(
        val tokenUri: String,
        val userInfoUri: String,
        val userNameAttribute: String?,
    )
}
