package wafflestudio.team4.reddit.domain.user.api

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
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
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.service.UserService
import wafflestudio.team4.reddit.global.auth.CurrentUser
import wafflestudio.team4.reddit.global.auth.JwtTokenProvider
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @GetMapping("/")
    fun getUsers() {
    }

    @GetMapping("/me/")
    @Transactional
    fun getCurrentUser(@CurrentUser user: User): UserDto.Response {
        return UserDto.Response(user)
    }

    @GetMapping("/{user_id}/")
    fun getUser(@PathVariable("user_id") id: Long): UserDto.Response {
        val user = userService.getUserById(id)
        return UserDto.Response(user)
    }

    @PostMapping("/")
    fun signup(@Valid @RequestBody signupRequest: UserDto.SignupRequest): ResponseEntity<UserDto.Response> {
        val user = userService.signup(signupRequest)
        val headers = HttpHeaders()
        headers.set("Authentication", jwtTokenProvider.generateToken(user.email))
        return ResponseEntity<UserDto.Response>(UserDto.Response(user), headers, HttpStatus.CREATED)
    }

    @PostMapping("/signin/")
    fun signin(@Valid @RequestBody signinRequest: UserDto.SigninRequest): ResponseEntity<UserDto.Response> {
        // TODO redirect filter to here
        val user = userService.signin(signinRequest)
        val headers = HttpHeaders()
        headers.set("Authentication", jwtTokenProvider.generateToken(user.email))
        return ResponseEntity<UserDto.Response>(UserDto.Response(user), headers, HttpStatus.OK)
    }

    @DeleteMapping("/me/")
    fun deleteUser(@CurrentUser user: User): ResponseEntity<String> {
        userService.deleteUser(user)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/me/")
    fun updateUser(
        @Valid @RequestBody updateRequest: UserDto.UpdateRequest,
        @CurrentUser user: User,
    ): UserDto.Response {
        val updatedUser = userService.updateUser(user, updateRequest)
        return UserDto.Response(updatedUser)
    }
}
