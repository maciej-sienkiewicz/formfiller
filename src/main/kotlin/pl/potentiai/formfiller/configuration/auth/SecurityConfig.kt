package pl.potentiai.formfiller.configuration.auth

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.OctetSequenceKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {

    private val secretKey = "superxtajnexhasloxsuperxtajnexhasloxsuperxtajnexhasloxsuper"
    private val key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtDecoder(): JwtDecoder =
        NimbusJwtDecoder
            .withSecretKey(key)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk = OctetSequenceKey.Builder(key.encoded).build()
        val jwkSet = JWKSet(jwk)
        return NimbusJwtEncoder(ImmutableJWKSet(jwkSet))
    }

    @Bean
    @Order(1) // Wyższy priorytet - ta konfiguracja zostanie sprawdzona przed główną konfiguracją
    fun actuatorSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/actuator/**") // Tylko dla actuator endpoints
            .authorizeHttpRequests { auth ->
                auth.anyRequest().authenticated()
            }
            .httpBasic {}
            .csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }

        return http.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        val user = User.builder()
            .username("2da7acd6-f281-470a-86bd-d91cb209fca1")
            .password(passwordEncoder().encode("87ba4b26-3a9d-4c85-b38f-16c75672a3fd"))
            .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    @Order(2) // Niższy priorytet - będzie sprawdzona po actuator security filter chain
    fun apiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/api/login", "/api/validate-token").permitAll()
                auth.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()) }
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }

        return http.build()
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        return converter
    }

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = listOf("*")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }
}