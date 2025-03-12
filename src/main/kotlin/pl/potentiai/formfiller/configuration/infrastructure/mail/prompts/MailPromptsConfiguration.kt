package pl.potentiai.formfiller.configuration.infrastructure.mail.prompts

import com.aallam.openai.client.OpenAI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.potentiai.formfiller.infrastructure.ai.prompt.mail.MailPromptRunner
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

@Configuration
class MailPromptsConfiguration {

    @Bean
    fun mailPromptRunner(openAI: OpenAI, timeableWrapper: TimeableWrapper): MailPromptRunner =
        MailPromptRunner(
            openAIClient = openAI, timeableWrapper
        )
}