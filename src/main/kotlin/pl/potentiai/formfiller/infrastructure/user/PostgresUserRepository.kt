package pl.potentiai.formfiller

import jakarta.persistence.*
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import pl.potentiai.formfiller.domain.common.User
import pl.potentiai.formfiller.domain.common.UserRepository

class PostgresUserRepository(private val userRepository: JpaUserRepository): UserRepository {

    override fun findByUsername(username: String): User? =
        userRepository.findByUsername(username)
            ?.toDomainObject()

    override fun shouldUserChangePassword(username: String): Boolean =
        userRepository.findByUsername(username)!!
            .shouldChangePassword

    override fun updatePassword(username: String, newPassword: String) {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("❌ Użytkownik $username nie istnieje!")

        user.passwordHash = newPassword
        user.shouldChangePassword = false
        userRepository.save(user)
        userRepository.flush()
    }

    private fun UserEntity.toDomainObject() =
        User(
            username = this.username,
            password = this.passwordHash
        )
}

interface JpaUserRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): UserEntity?
}

@Entity
@Table(name = "users")
class UserEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(unique = true, nullable = false)
    lateinit var username: String

    @Column(nullable = false)
    lateinit var passwordHash: String

    @Column(nullable = false)
    var shouldChangePassword: Boolean = false

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<RoleEntity> = mutableSetOf()
}

@Entity
@Table(name = "roles")
class RoleEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(unique = true, nullable = false)
    lateinit var name: String

    constructor(name: String) : this() {
        this.name = name
    }
}