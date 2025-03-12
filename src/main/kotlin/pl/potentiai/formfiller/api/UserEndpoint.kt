package pl.potentiai.formfiller.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.potentiai.formfiller.configuration.auth.JwtService
import pl.potentiai.formfiller.configuration.auth.UserService
import java.security.Principal

@RestController
@RequestMapping("/api")
class AuthController(private val userService: UserService, private val jwtService: JwtService) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, String>> {
        if (userService.authenticate(request.username, request.password)) {
            val token = jwtService.generateToken(request.username)
            return ResponseEntity.ok(mapOf(
                "token" to token,
                "shouldChangePassword" to userService.shouldUserChangePassword(request.username).toString()
                )
            )
        }
        return ResponseEntity.status(401).body(mapOf("error" to "Invalid credentials"))
    }

    @PostMapping("/change")
    fun change(@RequestBody request: ChangePasswordRequest): ResponseEntity<Unit> {
        userService.changePassword("admin", request.password)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/validate-token")
    fun validateToken(@RequestParam token: String): ResponseEntity<Map<String, String>> {
        return if (jwtService.validateToken(token)) {
            ResponseEntity.ok(mapOf("username" to jwtService.getUsernameFromToken(token)))
        } else {
            ResponseEntity.status(401).body(mapOf("error" to "Invalid token"))
        }
    }
}

data class LoginRequest(val username: String, val password: String)

data class ChangePasswordRequest(val password: String)