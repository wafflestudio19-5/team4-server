package wafflestudio.team4.reddit.domain.user.api

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import wafflestudio.team4.reddit.domain.user.dto.UserDto
import wafflestudio.team4.reddit.domain.user.service.UserService
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/")
    fun getUsers(

    ) {

    }

    @GetMapping("/me/")
    @Transactional
    fun getCurrentUser(

    ) {

    }

    @GetMapping("/{user_id}/")
    fun getUser(@PathVariable("user_id") id: Long) {

    }

    @PostMapping("/users/signup/")
    fun signup(@Valid @RequestBody signupRequest: UserDto.SignupRequest): ResponseEntity<UserDto.Response> {
        val user = userService.signup(signupRequest)
        val headers = HttpHeaders()
        // headers.set("Authentication", jwtTokenProvider.generateToken(user.email))
        return ResponseEntity.noContent().headers(headers).build()
    }

    @PostMapping("/users/signin/")
    fun signin() {

    }

    @DeleteMapping("/users/me/")
    fun deleteUser() {

    }

    @PutMapping("/users/me/")
    fun updateUser() {

    }
}
