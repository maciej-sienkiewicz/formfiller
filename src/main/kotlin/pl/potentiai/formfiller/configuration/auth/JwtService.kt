package pl.potentiai.formfiller.configuration.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class JwtService {

    private val secretKey = "superxtajnexhasloxsuperxtajnexhasloxsuperxtajnexhasloxsuper"
    private val key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    fun generateToken(username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusSeconds(3600*48)))
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun getUsernameFromToken(token: String): String {
        return getClaims(token).subject
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}