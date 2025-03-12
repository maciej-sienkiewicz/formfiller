package pl.potentiai.formfiller.configuration.infrastructure.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.potentiai.formfiller.JpaUserRepository
import pl.potentiai.formfiller.PostgresUserRepository
import pl.potentiai.formfiller.domain.common.UserRepository

@Configuration
class UserRepositoryConfiguration {

    @Bean
    fun postgresUserRepository(jpaUserRepository: JpaUserRepository): PostgresUserRepository =
        PostgresUserRepository(
            userRepository = jpaUserRepository
        )
}