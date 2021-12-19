package wafflestudio.team4.reddit.global.validation.constraints

import org.springframework.stereotype.Component
import wafflestudio.team4.reddit.domain.user.exception.UserAlreadyExistsException
import wafflestudio.team4.reddit.domain.user.repository.UserRepository
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

@Component
class UniqueEmailValidator(val userRepository: UserRepository) : ConstraintValidator<UniqueEmail, String> {
    override fun isValid(email: String?, context: ConstraintValidatorContext?): Boolean {
        if (email == null) {
            return true
        }
        val isDuplicate = userRepository.existsByEmail(email)

        if (isDuplicate) {
            throw UserAlreadyExistsException()
        }
        return !isDuplicate
    }
}
