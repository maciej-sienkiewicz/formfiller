package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common

import com.aallam.openai.client.OpenAI
import pl.potentiai.formfiller.domain.candidateprofile.model.Education
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

class EducationPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): Extractor<Educations>(openAI, Educations::class, timeableWrapper, "education") {

    override val PROMPT = """
Twoim zadaniem jest znalezienie sekcji z edukacją i wyekstraktowanie danych o pozycjach związanych z edukacją. 
Skup się tylko na edukacji: pomiń rekordy związane ze zdobytymi certyfikatami czy uprawnieniami. Skup się tylko na edukacji szkolnej.
Przygotuj odpowiedź w formacie JSON:

{
    "educations": [
        {
            "from": "YYYY",
            "to": "YYYY",
            "school": "String",
            "profession": "String",
            "typeOfSchoolEng": "String",
            "typeOfSchoolDe": "String"
        }
    ]
}


Zasady:
1. **"from"**: Rok rozpoczęcia edukacji w formacie "YYYY". Jeżeli brak - zostaw puste.
2. **"to"**: Rok zakończenia edukacji w formacie "YYYY". Jeśli edukacja trwa do dzisiaj, wpisz "2024". Jeżeli brak - zostaw puste.
3. **"school" i "city"**: Wartość odpowiadająca jednej z listy: ["Szkoła podstawowa", "Szkoła zawodowa", "Technikum", "Liceum", "Szkoła wyższa", "Studium policealne"]. Koniecznie dopasuj wartość. Nie dodawaj żadnego znaku. Wartość musi być wybrana z listy.
4. **"profession"**: Kierunek bądź specjalizacja - na przykład "Informatyka", "Budowa maszyn" etc.
5. **"typeOfSchoolEng"**: Przetłumaczona na j. angielski wartość z pola "school"
5. **"typeOfSchoolDe"**: Przetłumaczona na j. niemiecki wartość z pola "school"
8. Nie dodawaj żadnych dodatkowych informacji, opisów ani znaków poza oczekiwanym JSON.
    """.trimIndent()
}

data class Educations(val educations: List<Education>)