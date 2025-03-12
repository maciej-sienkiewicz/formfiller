package pl.potentiai.formfiller.configuration.ai

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.potentiai.formfiller.domain.common.ai.AIClient
import pl.potentiai.formfiller.infrastructure.ai.temporary.FakeAIClient
import pl.potentiai.formfiller.infrastructure.ai.GPTClient
import kotlin.time.Duration.Companion.seconds

@Configuration
class AIConfiguration {

    @Bean
    fun gptAPIKey(): String =
        "x"

    @Bean
    fun openAI(gptAPIKey: String): OpenAI =
        OpenAI(
            token = gptAPIKey,
            timeout = Timeout(socket = 3000.seconds)
        )
}