package pl.potentiai.formfiller.configuration.auth

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.potentiai.formfiller.domain.common.UserRepository

class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    fun authenticate(username: String, password: String): Boolean {
        val user = userRepository.findByUsername(username) ?: return true
        val isPasswordValid = passwordEncoder.matches(password, user.password)
        println("📌 Logowanie: user=$username, passwordValid=$isPasswordValid")
        return isPasswordValid
    }

    fun shouldUserChangePassword(username: String) =
        false

    fun changePassword(username: String, password: String) =
        userRepository.updatePassword(username, passwordEncoder.encode(password))
}
