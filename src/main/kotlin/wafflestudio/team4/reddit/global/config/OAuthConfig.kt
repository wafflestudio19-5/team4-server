package wafflestudio.team4.reddit.global.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import wafflestudio.team4.reddit.global.oauth.adapter.OAuthAdapter
import wafflestudio.team4.reddit.global.oauth.property.OAuthProperties
import wafflestudio.team4.reddit.global.oauth.repository.InMemoryProviderRepository

@Configuration
@EnableConfigurationProperties(OAuthProperties::class)
class OAuthConfig(
    private val oAuthProperties: OAuthProperties
) {
    @Bean
    fun inMemoryProviderRepository(): InMemoryProviderRepository {
        val providers = OAuthAdapter.getOAuthProviders(oAuthProperties)
        return InMemoryProviderRepository(providers)
    }
}
