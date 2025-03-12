package pl.potentiai.formfiller.configuration.domain

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pl.potentiai.formfiller.PostgresUserRepository
import pl.potentiai.formfiller.configuration.auth.UserService
import pl.potentiai.formfiller.domain.common.UserRepository

@Configuration
class UserServiceConfiguration {

    @Bean
    fun bean(userRepository: PostgresUserRepository, passwordEncoder: BCryptPasswordEncoder): UserService =
        UserService(
            userRepository = userRepository,
            passwordEncoder = passwordEncoder
        )
}