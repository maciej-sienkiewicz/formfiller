package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper
import kotlin.reflect.KClass
import io.micrometer.core.instrument.Tag

abstract class Extractor<T : Any>(
    private val openAIClient: OpenAI,
    private val targetClass: KClass<T>,
    private val timeableWrapper: TimeableWrapper,
    private val className: String
) {

    open fun model() = "gpt-4o-mini"

    val mapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())

    abstract val PROMPT: String

    suspend fun extractToObject(content: String): T =
        timeableWrapper.metricTimeExecution("prompt.executiontime", listOf(Tag.of("name", className))) {
            createGPTRequestToExtractData(content)
                .callGPTApi()
                .getResponseJson()
                ?.mapJsonToClassObject()
                ?: throw AIResponseMappingException("Can not map response")
        }

    private fun createGPTRequestToExtractData(cv: String): ChatCompletionRequest =
        ChatCompletionRequest(
            model = ModelId(model()),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = PROMPT
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = cv
                )
            )
        )

    private suspend fun ChatCompletionRequest.callGPTApi() =
        openAIClient.chatCompletion(this)

    private fun ChatCompletion.getResponseJson(): String? {
        val reseponse = (this.choices.first().message.messageContent as TextContent?)?.content
        println(reseponse)
        return reseponse
    }

    open fun String.mapJsonToClassObject(): T =
        mapper.readValue(this.getOnlyJson(), targetClass.java)

    private fun String.getOnlyJson(): String =
        this.substring(this.indexOfFirst { c -> c == '{' }, this.indexOfLast { c -> c == '}' }+1)
}

class AIResponseMappingException(message: String) : RuntimeException(message)