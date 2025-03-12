package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

/**
 * Data model based on Vision API docs:
 * https://api.openai.com/v1/chat/completions
 *
 * {
 *   "model": "gpt-4o-mini",
 *   "messages": [
 *     {
 *       "role": "user",
 *       "content": [
 *         { "type": "text", "text": "What is in this image?" },
 *         {
 *           "type": "image_url",
 *           "image_url": {
 *             "url": "...",
 *             "detail": "high" // or "low"/"auto"/omit entirely
 *           }
 *         }
 *       ]
 *     }
 *   ],
 *   "max_tokens": 300
 * }
 */

// Represents the entire request body
private data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int
)

// Represents a single message, which has a role and a content array
data class Message(
    val role: String,
    val content: List<ContentItem>
)

// Represents one item in the message's content array
sealed class ContentItem {
    data class Text(
        val type: String = "text",
        val text: String
    ) : ContentItem()

    data class ImageUrl(
        val type: String = "image_url",
        val image_url: ImageUrlContent
    ) : ContentItem()
}

// The "image_url" object inside an ImageUrl content item
data class ImageUrlContent(
    val url: String,
    val detail: String? = null
)

@Service
class RESTContentReader(
    private val restTemplate: RestTemplate,
    private val apiKey: String
) {

    private val objectMapper = jacksonObjectMapper()


    fun getChatCompletion(x: String): String? {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $apiKey")
        }

        // Build the request in the exact "Vision" format
        val requestBody = ChatCompletionRequest(
            model = "gpt-4o-mini",
            messages = listOf(
                Message(
                    role = "user",
                    content = listOf(
                        ContentItem.Text(
                            // "type": "text", "text": "What is in this image?"
                            text = "Process this image and perform Optical Character Recognition (OCR) to extract all the text. Do not summarize or interpret the content—just return the exact text as recognized from the image. If there are any recognition errors, provide the text as accurately as possible. Do not add any extra comments or explanations."
                        ),
                        ContentItem.ImageUrl(
                            // "type": "image_url"
                            image_url = ImageUrlContent(
                                // "url": "https://..."
                                url = x,
                            )
                        )
                    )
                )
            ),
            max_tokens = 10000
        )

        val httpEntity = HttpEntity(requestBody, headers)
        val url = "https://api.openai.com/v1/chat/completions"

        val response = restTemplate.postForEntity(url, httpEntity, String::class.java)
        return extractContent(response.body!!)
    }

    fun extractContent(jsonString: String): String {
        val rootNode: JsonNode = objectMapper.readTree(jsonString)

        return rootNode["choices"]?.get(0)?.get("message")?.get("content")?.asText() ?: "Brak treści"
    }

}
