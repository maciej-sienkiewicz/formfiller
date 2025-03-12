package pl.potentiai.formfiller.domain.mail

import pl.potentiai.formfiller.api.EmailHelperRequest
import pl.potentiai.formfiller.domain.common.ai.AIClient

class MailService(
    private val aiClient: AIClient,
) {

    suspend fun prepareEmail(request: EmailHelperRequest): EmailHelperResponse =
        aiClient.prepareEmail(request)
}

data class EmailHelperResponse(val content: String)