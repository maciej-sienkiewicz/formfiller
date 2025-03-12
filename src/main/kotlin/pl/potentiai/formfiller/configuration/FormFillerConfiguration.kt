package pl.potentiai.formfiller.configuration

import com.aallam.openai.client.OpenAI
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.potentiai.formfiller.api.validation.CompositeFileValidator
import pl.potentiai.formfiller.api.validation.EmptyFileValidator
import pl.potentiai.formfiller.api.validation.FileValidator
import pl.potentiai.formfiller.api.validation.SupportedContentType
import pl.potentiai.formfiller.domain.FormFillerFacade
import pl.potentiai.formfiller.domain.candidateprofile.CandidateProfileService
import pl.potentiai.formfiller.domain.common.ai.AIClient
import pl.potentiai.formfiller.infrastructure.ai.prompt.mail.MailPromptRunner
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

@Configuration
class FormFillerConfiguration {

    @Bean
    fun formFillerFacade(candidateProfileService: CandidateProfileService, mailPromptRunner: MailPromptRunner, aiClient: AIClient, openAI: OpenAI, timeableWrapper: TimeableWrapper): FormFillerFacade =
        FormFillerFacade(candidateProfileService, mailPromptRunner, aiClient, openAI, timeableWrapper)

    @Bean
    @Primary
    fun compositeFileValidator(validators: List<FileValidator>): FileValidator =
        CompositeFileValidator(validators)

    @Bean
    fun emptyFileValidator(): FileValidator =
        EmptyFileValidator()

    @Bean
    fun supportedContentTypeValidator(): FileValidator =
        SupportedContentType()
}