package pl.potentiai.formfiller.domain.common

interface UserRepository {
    fun findByUsername(username: String): User?
    fun shouldUserChangePassword(username: String): Boolean
    fun updatePassword(username: String, newPassword: String)
}

data class User(val username: String, val password: String)