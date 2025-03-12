package pl.potentiai.formfiller.infrastructure.ai

import pl.potentiai.formfiller.api.EmailHelperRequest
import pl.potentiai.formfiller.domain.common.ai.AIClient
import pl.potentiai.formfiller.domain.candidateprofile.model.CandidateProfile
import pl.potentiai.formfiller.domain.mail.EmailHelperResponse
import pl.potentiai.formfiller.infrastructure.ai.mapper.toCleanDomainObject
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.origin.CandidateProfilePromptRunner
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common.PromptResultToDomainObjectMapper.toDomainObject
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common.RESTContentReader
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.origin.ICandidateProfilePromptRunner
import pl.potentiai.formfiller.infrastructure.ai.prompt.mail.MailPromptRunner
import pl.potentiai.formfiller.infrastructure.ai.prompt.translation.TranslationPromptRunner

class GPTClient(
    private val candidateProfilePromptRunner: ICandidateProfilePromptRunner,
    private val mailPromptRunner: MailPromptRunner,
    private val translationPromptRunner: TranslationPromptRunner,
    private val restContentReader: RESTContentReader,
): AIClient {

    override suspend fun fromRawTextToCandidateProfile(content: String): CandidateProfile =
        candidateProfilePromptRunner.run(content)
            .toDomainObject()
            .toCleanDomainObject()

    override suspend fun prepareEmail(request: EmailHelperRequest): EmailHelperResponse =
        EmailHelperResponse(mailPromptRunner.create(request) ?: "")

    override suspend fun translate(content: String): String =
        translationPromptRunner.run(content) ?: ""

    override suspend fun extractContent(base64: String): String =
        restContentReader.getChatCompletion(base64)!!
}