package pl.potentiai.formfiller.configuration.ai

import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common.AIResponseMappingException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException(
        ex: ExpiredJwtException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val errorBody = mapOf(
            "error" to "TokenExpired",
            "message" to "Twój token wygasł. Zaloguj się ponownie.",
            "details" to (ex.localizedMessage ?: "Token wygasł"),
            "path" to request.requestURI
        )
        return ResponseEntity(errorBody, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(AIResponseMappingException::class)
    fun handleAIExpcetion(
        ex: AIResponseMappingException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val errorBody = mapOf(
            "error" to "AIException",
            "message" to "Aktualnie model AI jest niedostępny - monitorujemy sytuację.",
            "details" to "Aktualnie model AI jest niedostępny - monitorujemy sytuację",
            "path" to request.requestURI
        )
        return ResponseEntity(errorBody, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}


