package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.splitted

import com.aallam.openai.client.OpenAI
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common.Extractor
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

class ExperienceLocationPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): Extractor<Test>(openAI, Test::class, timeableWrapper, "experience-location") {

    override fun model(): String = "gpt-4o"

    override val PROMPT = """
Z przesłanego dokumentu wyekstraktuj  informacje o KAŻDEJ JEDNEJ pozycji w doświadczeniu. Niczego nie pomijaj.

{
    "experience": [
        {
            "index": String,
            "dateFrom": String,
            "dateTo": String,
            "country": String,
            "city" String
        }
    ]
}


Zasady.
1. Obowiązkowo wstaw index dla każdej pozycji w sekcji "CV"
2. **index** - pozycja w CV
3. **"dateFrom"**: Rok rozpoczęcia pracy. Dostosuj do formatu: "YYYY-MM".
4. **"dateTo"**: Rok zakończenia pracy. Dostosuj do formatu: "YYYY-MM". Jeśli praca trwa do dzisiaj, wpisz "2025-02".
5. **"city"**: Wstaw tylko jeśli dana pozycja zawiera informacje o mieście. Nie uwzględniaj poprzednich pozycji. Jeśli brak informacji: wstaw pusty string.
6. **"country"**: Wstaw tylko jeśli dana pozycja zawiera informacje o kraju. Nie uwzględniaj poprzednich pozycji. Jeśli brak informacji: wstaw pusty string. Wstaw w języku polskim.
    """.trimIndent()
}

data class Test(val experience: List<Experience>)

data class Experience(val index: String, val dateFrom: String, val dateTo: String, val city: String, val country: String)