package wafflestudio.team4.reddit.domain.user.api

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import wafflestudio.team4.reddit.domain.user.dto.UserDto
import wafflestudio.team4.reddit.domain.user.service.UserService
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/")
    fun getUsers() {
    }

    @GetMapping("/me/")
    @Transactional
    fun getCurrentUser() {
    }

    @GetMapping("/{user_id}/")
    fun getUser(@PathVariable("user_id") id: Long) {
    }

    @PostMapping("/")
    fun signup(@Valid @RequestBody signupRequest: UserDto.SignupRequest): ResponseEntity<UserDto.Response> {
        val user = userService.signup(signupRequest)
        val headers = HttpHeaders()
        // headers.set("Authentication", jwtTokenProvider.generateToken(user.email))
        return ResponseEntity.noContent().headers(headers).build()
    }

    @PostMapping("/signin/")
    fun signin() {
    }

    @DeleteMapping("/me/")
    fun deleteUser() {
    }

    @PutMapping("/me/")
    fun updateUser() {
    }
}
