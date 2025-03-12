package pl.potentiai.formfiller.api

import io.ktor.util.collections.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.potentiai.formfiller.api.model.CandidateProfileDTO
import pl.potentiai.formfiller.api.model.toDTO
import pl.potentiai.formfiller.domain.FormFillerFacade
import pl.potentiai.formfiller.domain.sms.SmsService
import pl.potentiai.formfiller.infrastructure.metrics.CountUsage
import java.security.Principal
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/api/form-filler")
class FormFillerEndpoint(
    private val formFillerFacade: FormFillerFacade,
    private val smsService: SmsService,
) {

    @PostMapping("upload")
    @CountUsage(metricName = "api.upload_file")
    suspend fun uploadFile(@RequestParam("file") file: MultipartFile, principal: Principal): ResponseEntity<CandidateProfileDTO> {
        val candidateProfile = formFillerFacade.createCandidateProfile(file)
        return ResponseEntity.ok(candidateProfile.toDTO())
    }

    @PostMapping("email-helper")
    @CountUsage(metricName = "api.email_helper")
    suspend fun emailHelper(@RequestBody input: EmailHelperRequest, principal: Principal): ResponseEntity<EmailDTO> {
        return ResponseEntity.ok(formFillerFacade.prepareEmail(input))
    }

    @GetMapping("test")
    suspend fun emailHelperr(): ResponseEntity<String> {
        return ResponseEntity.ok(formFillerFacade.test())
    }

    @PostMapping("email-helper/translation")
    @CountUsage(metricName = "api.email-helper/translation")
    suspend fun translate(@RequestBody content: String, principal: Principal): ResponseEntity<String> =
        ResponseEntity.ok(formFillerFacade.translate(content))

    @GetMapping("/stats")
    fun getStats(): ResponseEntity<Map<String, Any>> {
        val stats = formFillerFacade.getStats()
        return ResponseEntity.ok(stats)
    }

    @PostMapping("/sms/send")
    @CountUsage(metricName = "api.sms/send")
    fun sendSms(
        @RequestBody request: SmsSendRequest): ResponseEntity<String> {
        return try {
            smsService.sendSms(request.name, request.templateId, request.sender, request.recipients)
            ResponseEntity.ok("SMS został wysłany pomyślnie")
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Wystąpił błąd podczas wysyłania SMS-a")
        }
    }

    @GetMapping("/sms/templates")
    @CountUsage(metricName = "api.sms/templates")
    fun getSMSTemplates(): ResponseEntity<SMSTemplatesResponse> =
        smsService.getTemplates()
            .map { SMSTemplatesResponse.Template(it.id, it.name, it.content) }
            .let { SMSTemplatesResponse(it) }
            .let { ResponseEntity.ok(it) }
}

data class EmailHelperRequest(val context: String?, val language: String, val content: String)

data class EmailDTO(val content: String)

data class SmsSendRequest(
    val name: String,
    val templateId: Long,
    val sender: String,
    val recipients: List<String>
)

data class SMSTemplatesResponse(val elements: List<Template>) {
    data class Template(val id: Long, val name: String, val content: String)
}