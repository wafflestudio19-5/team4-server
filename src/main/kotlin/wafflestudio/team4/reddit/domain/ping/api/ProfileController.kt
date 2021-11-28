package wafflestudio.team4.reddit.domain.ping.api

import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("")
class ProfileController(
    private val env: Environment
) {
    @GetMapping("/profile")
    fun profile(): String {
        val profiles: List<String> = env.activeProfiles.toList()
        val realProfiles: List<String> = listOf("prod1", "prod2")
        val defaultProfile: String = if (profiles.isEmpty()) "default" else profiles[0]

        // return one of real, real1, real2 or default
        return profiles.stream()
            .filter(realProfiles::contains)
            .findAny()
            .orElse(defaultProfile)
    }
}
