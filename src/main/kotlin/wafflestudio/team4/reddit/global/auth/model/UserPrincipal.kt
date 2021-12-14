package wafflestudio.team4.reddit.global.auth.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import wafflestudio.team4.reddit.domain.user.model.User

class UserPrincipal(val user: User) : UserDetails {
    override fun getUsername(): String {
        return user.email
    }

    override fun getPassword(): String? {
        return user.password
    }

    override fun getAuthorities(): List<GrantedAuthority> {
        val roles: List<String> = user.roles.split(",").filter { it.isNotEmpty() }
        return roles.map { role: String? -> SimpleGrantedAuthority(role) }
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
