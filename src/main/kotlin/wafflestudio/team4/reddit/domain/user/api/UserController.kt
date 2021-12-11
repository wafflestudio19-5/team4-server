package wafflestudio.team4.reddit.domain.user.api

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import wafflestudio.team4.reddit.domain.user.service.UserService

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/")
    fun getSeminars(

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
    fun signup() {

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
