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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import wafflestudio.team4.reddit.domain.user.dto.UserDto
import wafflestudio.team4.reddit.domain.user.model.User
import wafflestudio.team4.reddit.domain.user.service.UserService
import wafflestudio.team4.reddit.global.auth.annotation.CurrentUser
import wafflestudio.team4.reddit.global.auth.jwt.JwtTokenProvider
import wafflestudio.team4.reddit.global.common.dto.PageLinkDto
import wafflestudio.team4.reddit.global.common.dto.PageResponse
import java.lang.Long.max
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @GetMapping("/")
    fun getUsersPage(
        @RequestParam(required = false, defaultValue = Long.MAX_VALUE.toString()) lastUserId: Long,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false) keyword: String?,
    ): PageResponse<UserDto.Response> {
        // TODO order
        // TODO deleted users
        val usersPage = userService.getUsersPage(lastUserId, size, keyword)
        val userLinks = buildPageLink(lastUserId, size, keyword)
        return PageResponse(usersPage.map { UserDto.Response(it) }, userLinks)
    }

    @GetMapping("/name/")
    fun getUsernamesPage(
        @RequestParam(required = false, defaultValue = Long.MAX_VALUE.toString()) lastUserId: Long,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false) keyword: String?,
    ): PageResponse<UserDto.UsernameResponse> {
        // TODO order
        // TODO deleted users
        val usersPage = userService.getUsersPage(lastUserId, size, keyword)
        val userLinks = buildPageLink(lastUserId, size, keyword)
        return PageResponse(usersPage.map { UserDto.UsernameResponse(it) }, userLinks)
    }

    private fun buildPageLink(lastUserId: Long, size: Int, keyword: String?): PageLinkDto {
        // TODO refactor
        val first = "size=$size"
        val self = "lastUserId=$lastUserId&size=$size"
        val last = "lastUserId=${size + 1}&size=$size"

        val next = "lastUserId=${max(0, lastUserId - size)}&size=$size"
        val prev = "lastUserId=" +
            "${if ((lastUserId - Long.MAX_VALUE) + size > 0) Long.MAX_VALUE else lastUserId + size}&size=$size"

        return if (keyword == null) {
            PageLinkDto(first, prev, self, next, last)
        } else {
            PageLinkDto(
                "$first&keyword=$keyword", "$prev&keyword=$keyword",
                "$self&keyword=$keyword", "$next&keyword=$keyword", "$last&keyword=$keyword"
            )
        }
    }

    @GetMapping("/me/")
//    @Transactional
    fun getCurrentUser(@CurrentUser user: User): UserDto.Response {
        val mergedUser = userService.mergeUser(user) // Response로 넘어갈 때 persistence context에 있어야함
        return UserDto.Response(mergedUser)
    }

    @GetMapping("/{user_id}/")
    fun getUser(@PathVariable("user_id") id: Long): UserDto.Response {
        val user = userService.getUserById(id)
        return UserDto.Response(user)
    }

    @PostMapping("/")
    @Transactional
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

    @PutMapping("/me/")
    @Transactional
    fun updateUser(
        @Valid @RequestBody updateRequest: UserDto.UpdateRequest,
        @CurrentUser user: User,
    ): UserDto.Response {
        val updatedUser = userService.updateUser(user, updateRequest)
        return UserDto.Response(updatedUser)
    }

    @DeleteMapping("/me/")
    @Transactional
    fun deleteUser(@CurrentUser user: User): ResponseEntity<String> {
        userService.deleteUser(user)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/profile/{user_id}/")
    fun getProfile(@PathVariable("user_id") id: Long): UserDto.ProfileResponse {
        val profile = userService.getProfileById(id)
//        val followNum = userService.getFollowNumById(id)
//        return UserDto.ProfileResponse(profile, followNum)
        return UserDto.ProfileResponse(profile)
    }

    @GetMapping("/profile/me/")
    fun getCurrentProfile(@CurrentUser user: User): UserDto.ProfileResponse {
        val profile = userService.getProfileById(user.id)
//        val followNum = userService.getFollowNumById(user.id)
//        return UserDto.ProfileResponse(profile, followNum)
        return UserDto.ProfileResponse(profile)
    }

    @GetMapping("/profile/image/")
    fun getProfileImageS3Url(
        @CurrentUser user: User,
        @Valid @RequestParam(required = true) filename: String
    ): UserDto.UploadImageResponse {
        val preSignedUrl = userService.getPresignedUrlAndSaveImage(user, filename)
        val imageUrl = "https://waffle-team-4-server-s3.s3.ap-northeast-2.amazonaws.com/profiles/" +
            "${user.id}/$filename"
        return UserDto.UploadImageResponse(preSignedUrl, imageUrl)
    }

    @PutMapping("/profile/me/")
    @Transactional
    fun updateProfile(
        @CurrentUser user: User,
        @Valid @RequestBody updateProfileRequest: UserDto.UpdateProfileRequest
    ): UserDto.ProfileResponse {
        val updatedProfile = userService.updateProfile(user, updateProfileRequest)
//        val followNum = userService.getFollowNumById(user.id)
//        return UserDto.ProfileResponse(updatedProfile, followNum)
        return UserDto.ProfileResponse(updatedProfile)
    }
}
