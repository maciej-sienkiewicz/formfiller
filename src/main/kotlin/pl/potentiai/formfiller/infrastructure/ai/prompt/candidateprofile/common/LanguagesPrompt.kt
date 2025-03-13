package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common

import com.aallam.openai.client.OpenAI
import pl.potentiai.formfiller.domain.candidateprofile.model.KnowledgeOfLanguages
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

class LanguagesPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): Extractor<KnowledgeOfLanguages>(openAI, KnowledgeOfLanguages::class, timeableWrapper, "languages" ) {
    override val PROMPT: String = """
Twoim zadaniem jest przetworzenie tekstu wejściowego i wyekstraktowanie informacji o językach, w jakich posługuje się osoba, oraz dobranie odpowiedniego poziomu znajomości języka z poniższej listy:

- "słabo komunikatywnie (A1/A2)"
- "komunikatywnie (A2/B1)"
- "dobrze-komunikatywnie (B1/B2)"
- "bardzo dobry (C1)"

Wynik powinien być przedstawiony wyłącznie w poniższej strukturze JSON:

{
    "languages": [
        {
            "name": "język",
            "languageLevel": "poziom"
        }
    ]
}

Zasady:
1. Pole "name" powinno zawierać nazwę języka w języku polskim i zaczynać się z dużej litery, przykład: "Niemiecki"
2. Pole "languageLevel" powinno zawierać najtrafniejszy poziom z listy powyżej na podstawie tekstu wejściowego.
3. Wartość wstawiona w "languageLevel" koniecznie musi odpowiadać jednej wartości z: ["słabo komunikatywnie (A1/A2)", "komunikatywnie (A2/B1)", "dobrze-komunikatywnie (B1/B2)", "bardzo dobry (C1)"]
4. Jeśli tekst nie podaje bezpośredniego poziomu, oszacuj najbardziej odpowiedni poziom na podstawie kontekstu (np. opisy typu "podstawy" to "słabo komunikatywnie (A1/A2)", a "biegły" to "bardzo dobry (C1)").
5. Jeśli w tekście brak jest jakichkolwiek informacji o językach, zwróć pustą tablicę w polu "languages".
6. Nie dodawaj żadnych dodatkowych informacji, opisów ani znaków poza oczekiwanym JSON.
""".trimIndent()
}
