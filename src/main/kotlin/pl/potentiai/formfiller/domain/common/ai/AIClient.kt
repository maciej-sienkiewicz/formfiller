package pl.potentiai.formfiller.domain.common.ai

import pl.potentiai.formfiller.api.EmailHelperRequest
import pl.potentiai.formfiller.domain.candidateprofile.model.CandidateProfile
import pl.potentiai.formfiller.domain.mail.EmailHelperResponse

interface AIClient {
    suspend fun fromRawTextToCandidateProfile(content: String): CandidateProfile
    suspend fun prepareEmail(request: EmailHelperRequest): EmailHelperResponse
    suspend fun translate(content: String): String
    suspend fun extractContent(base64: String): String
}