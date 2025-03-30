package pl.potentiai.formfiller.infrastructure.ai.prompt.mail

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import pl.potentiai.formfiller.api.EmailHelperRequest
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

class MailPromptRunner(private val openAIClient: OpenAI, private val timeableWrapper: TimeableWrapper) {

    companion object {
        private const val ROLE_PROMPT = """
Jesteś doświadczonym Starszym Rekruterem z 15-letnim stażem w branży rekrutacyjnej, 
specjalizującym się w profesjonalnej komunikacji biznesowej. 
Twoje wieloletnie doświadczenie w kontaktach z kandydatami i klientami ukształtowało 
precyzyjny, empatyczny i skuteczny styl komunikacji.

Charakteryzuje Cię:
- Precyzyjny, ale ciepły ton komunikacji
- Umiejętność dostosowania stylu do kontekstu branżowego
- Troska o budowanie pozytywnych relacji
- Jasne, zwięzłe i merytoryczne przekazy
    
**WAŻNE ZASADY:** 
- Wiadomość musi być kulturalna, uprzejma i profesjonalna.
- Nie może zawierać:
    - Wulgaryzmów, przekleństw ani obraźliwego języka.
    - Treści erotycznych lub nieprzyzwoitych.
    - Treści drastycznych, brutalnych lub związanych z przemocą.
    - Treści politycznie niepoprawnych ani potencjalnie obraźliwych (np. dotyczących kwestii religijnych, etnicznych, zdrowia psychicznego, samookaleczenia).
- Odpowiedź ma zawierać wyłącznie treść gotowego maila – bez żadnych komentarzy, objaśnień czy dodatkowych zwrotów.
- Nie dodawaj żadnych nagłówków (np. "Subject: ...") – generuj wyłącznie treść wiadomości.
- Mail powinien kończyć się jedynie zwrotem grzecznościowym (np. "Z pozdrowieniami" lub "Best regards"), bez dodatkowych danych (takich jak podpis, nazwa firmy, stanowisko).
- Jeśli nie znasz płci odbiorcy to unikaj bezpośrednich zwrotów jak ("Państwa", "Pani/Pana")
    
**DODATKOWE WYTYCZNE:**
- Dostosuj ton i styl do specyfiki branży i rodzaju komunikacji
- Używaj profesjonalnego, ale nie sztywnego języka
- Zachowaj balans między merytorycznością a uprzejmością
- Unikaj nadmiernie formalnego lub zbyt potocznego języka
"""


        private const val CONTEXT_PROMPT = """
Weź pod uwagę kontekst poprzedniej korespondencji:
- Przeanalizuj ton i treść dotychczasowej wymiany
- Zachowaj spójność stylistyczną z wcześniejszą komunikacją
- Uwzględnij kluczowe wątki i ustalenia z poprzednich wiadomości
"""
        private const val LANGUAGE_PROMPT = """
Maila napisz w języku: {język}

Dodatkowe wskazówki:
- Dostosuj poziom formalności do branży i odbiorcy
- Zachowaj naturalny, płynny język
- Unikaj wieloznaczności i zbędnych ozdobników
"""
        private const val MAIL_REQUEST = """
Opracuj treść profesjonalnego maila dotyczącego: 

Wskazówki do przygotowania treści:
- Jasno określ cel wiadomości
- Przedstaw kluczowe informacje zwięźle i klarownie
- Zastosuj logiczną strukturę: wprowadzenie, główna treść, podsumowanie/call to action
- Dostosuj język do specyfiki tematu i potencjalnego odbiorcy
"""    }

    suspend fun create(input: EmailHelperRequest): String? =
        timeableWrapper.metricTimeExecution("prompt.executiontime.mail", emptyList()) {
            val messages = buildPromptMessages(input)
            val extractResponse = createGPTRequest(messages)
                .callGPTApi()
                .extractResponse()
            extractResponse
        }

    private fun buildPromptMessages(input: EmailHelperRequest): List<ChatMessage> = buildList {
        add(systemMessage(ROLE_PROMPT))
        input.context?.takeIf { it.isNotEmpty() }?.let {
            add(systemMessage(CONTEXT_PROMPT + it))
        }
        add(systemMessage(LANGUAGE_PROMPT + input.language))
        add(userMessage(MAIL_REQUEST + input.content))
    }

    private fun systemMessage(content: String) = ChatMessage(role = ChatRole.System, content = content)
    private fun userMessage(content: String) = ChatMessage(role = ChatRole.User, content = content)

    private suspend fun ChatCompletionRequest.callGPTApi() = openAIClient.chatCompletion(this)

    private fun ChatCompletion.extractResponse(): String? =
        choices.firstOrNull()?.message?.messageContent?.let { (it as? TextContent)?.content }

    private fun createGPTRequest(messages: List<ChatMessage>) = ChatCompletionRequest(
        model = ModelId("gpt-4o-mini"),
        messages = messages
    )
}