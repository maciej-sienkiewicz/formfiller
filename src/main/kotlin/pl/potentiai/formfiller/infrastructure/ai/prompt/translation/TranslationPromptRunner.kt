package pl.potentiai.formfiller.infrastructure.ai.prompt.translation

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

class TranslationPromptRunner(private val openAIClient: OpenAI, private val timeableWrapper: TimeableWrapper) {

    private val PROMPT = """
    Jesteś tłumaczem tekstu na język polski.
    1) Jeżeli tekst na wejściu jest po polsku - po prostu go prześlij w odpowiedzi.
    2) Jeżeli tekst jest w innym języku - przetłumacz go na język polski.
    3) W odpowiedzi nie dodawaj żadnych komentarzy, znaczników, ani żadnych dodatkowych znaków.
    4) Odpowiedz wyłącznie przetłumaczonym tekstem – bez kluczy, cudzysłowów, nawiasów ani jakiejkolwiek struktury JSON.
""".trimIndent()

    suspend fun run(content: String): String? =
        timeableWrapper.metricTimeExecution("prompt.executiontime.mail", emptyList()) {
            val messages = buildPromptMessages(content)
            val extractResponse = createGPTRequest(messages)
                .callGPTApi()
                .extractResponse()
            extractResponse
        }

    private fun buildPromptMessages(content: String): List<ChatMessage> = buildList {
        add(systemMessage(PROMPT))
        add(userMessage(content))
    }

    private fun systemMessage(content: String) = ChatMessage(role = ChatRole.System, content = content)
    private fun userMessage(content: String) = ChatMessage(role = ChatRole.User, content = content)

    private suspend fun ChatCompletionRequest.callGPTApi() = openAIClient.chatCompletion(this)

    private fun ChatCompletion.extractResponse(): String? {
        val x = choices.firstOrNull()?.message?.messageContent?.let { (it as? TextContent)?.content }
        println(x)
        println("Nowa linia")
        println(choices.firstOrNull()?.message?.messageContent)
        return x
    }

    private fun createGPTRequest(messages: List<ChatMessage>) = ChatCompletionRequest(
        model = ModelId("gpt-4o-mini"),
        messages = messages
    )
}