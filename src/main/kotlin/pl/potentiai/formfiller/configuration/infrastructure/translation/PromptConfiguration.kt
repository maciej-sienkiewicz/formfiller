package pl.potentiai.formfiller.configuration.infrastructure.translation

import com.aallam.openai.client.OpenAI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.potentiai.formfiller.infrastructure.ai.prompt.translation.TranslationPromptRunner
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

@Configuration
class PromptConfiguration {

    @Bean
    fun translationPromptRunner(openAI: OpenAI, timeableWrapper: TimeableWrapper): TranslationPromptRunner =
        TranslationPromptRunner(openAI, timeableWrapper)
}