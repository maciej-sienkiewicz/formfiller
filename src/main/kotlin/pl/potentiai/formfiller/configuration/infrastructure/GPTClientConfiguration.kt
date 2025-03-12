package pl.potentiai.formfiller.configuration.infrastructure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.potentiai.formfiller.domain.common.ai.AIClient
import pl.potentiai.formfiller.infrastructure.ai.GPTClient
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.origin.CandidateProfilePromptRunner
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common.RESTContentReader
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.origin.ICandidateProfilePromptRunner
import pl.potentiai.formfiller.infrastructure.ai.prompt.mail.MailPromptRunner
import pl.potentiai.formfiller.infrastructure.ai.prompt.translation.TranslationPromptRunner
import pl.potentiai.formfiller.infrastructure.ai.temporary.FakeAIClient

@Configuration
class GPTClientConfiguration {

    @Bean
    @Primary
    fun gptClient(
        candidateProfilePromptRunner: ICandidateProfilePromptRunner,
        mailPromptRunner: MailPromptRunner,
        translationPromptRunner: TranslationPromptRunner,
        restContentReader: RESTContentReader,
        ): AIClient =
        GPTClient(candidateProfilePromptRunner, mailPromptRunner, translationPromptRunner, restContentReader)

    @Bean
    fun fakeClient(): AIClient =
        FakeAIClient()
}