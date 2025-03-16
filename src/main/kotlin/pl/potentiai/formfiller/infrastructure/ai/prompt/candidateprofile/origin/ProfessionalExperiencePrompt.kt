package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.origin

import com.aallam.openai.client.OpenAI
import pl.potentiai.formfiller.domain.candidateprofile.model.Task
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common.Extractor
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

class ProfessionalExperiencePrompt(openAI: OpenAI,timeableWrapper: TimeableWrapper): Extractor<ProfessionalExperiences>(openAI, ProfessionalExperiences::class, timeableWrapper, "professionalExperience") {

    override fun String.mapJsonToClassObject(): ProfessionalExperiences {
        val jsonString = this.trim().let { json ->
            if (json.startsWith("[")) {
                "{\"experiences\": $json}"
            } else {
                json
            }
        }
        return mapper.readValue(jsonString, ProfessionalExperiences::class.java)
    }

            override val PROMPT = """
Z przesłanego dokumentu wyekstraktuj  informacje o KAŻDEJ JEDNEJ pozycji w doświadczeniu. Nie halucynuj.

{
    "experiences": [
        {
            "dateFrom": "YYYY-MM",
            "dateTo": "YYYY-MM",
            "country": "String",
            "city": "String",
            "company": "String",
            "profession": "String",
            "professionDe": "String",
            "professionEng": "String",
            "experienceTask": [
                {
                    "name": "String"
                }
            ]
        }
    ]
}


Zasady:
1. **"dateFrom"**: Rok rozpoczęcia pracy. Dostosuj do formatu: "YYYY-MM".
2. **"dateTo"**: Rok zakończenia pracy. Dostosuj do formatu: "YYYY-MM". Jeśli praca trwa do dzisiaj, wpisz "2025-02".
3. **"city"**: Miasto w którym była wykonywana praca 
4. **"country"**: Jeśli jest bezpośrednio podany kraj: wstaw wartość. Inaczej jeśli jest wskazane miasto wyszukaj kraj gdzie znajduje się to miasto i wstaw w języku polskim.
5. **"company"**: Nazwa firmy.
6. **"profession"**: Wartość MUSI być wzięta z listy przesłanej poniżej. Lista będzie oznaczona w tagach <list></list>. Dobierz tylko jedną najtrafniejszą wartość.
7. **professionDe"**: Wykonywany zawód dla tego okresu czasu - w języku niemieckim
7. **professionEng"**: Wykonywany zawód dla tego okresu czasu - w języku angielskim
8. **"experienceTask"**: Lista zadań opisanych w pracy, np. obowiązki, projekty. Dla każdego zadania utwórz osobny obiekt z polem "name".
9. Jeśli w tekście brakuje informacji o dacie, kraju, mieście lub zadaniach, zostaw puste wartości dla tych pól.
10. Nie dodawaj żadnych dodatkowych informacji, opisów ani znaków poza oczekiwanym JSON.
11. Upewnij się, że pole **profession** nie jest puste i zostało wybrane z listy. 

Dostępna lista wartości dla "profession":
<list>
        Spawacz elektrodą 111
        Spawacz metodą gazową 311
        Spawacz MIG/MAG
        Spawacz 138 – spawanie drutem proszkowym z rdzeniem metalicznym
        Spawacz TIG
        Ślusarz
        Ślusarz rurowy
        Ślusarz narzędziowy
        Operator palnika gazowego
        Spawacz tworzyw sztucznych
        Monter konstrukcji stalowych
        Monter wind
        Elektryk budowlany
        Elektryk przemysłowy - podłączanie szaf sterowniczych
        Elektryk okrętowy
        Monter szaf sterowniczych
        Elektromechanik
        Monter instalacji alarmowych
        Monter ogrzewania gazowego
        Monter ogrzewania olejowego
        Monter systemów wodnych
        Monter systemów kanalizacyjnych
        Monter (biały montaż)
        Monter klimatyzacji
        Monter wentylacji
        Monter systemów chłodniczych
        Monter zraszaczy
        Stolarz meblowy
        Stolarz-monter budowlany
        Stolarz-pracownik produkcji (okna, drzwi)
        Cieśla dachowy (więźba dachowa)
        Cieśla szalunkowy (wykonywanie szalunków)
        Cieśla okrętowy (naprawy statków)
        Dekarz
        Pracownik Prefabrykacji
        Murarz
        Kamieniarz
        Malarz
        Monter suchej zabudowy
        Płytkarz
        Posadzkarz
        Brukarz
        Monter fasad
        Mechanik samochodów osobowych
        Mechanik samochodów ciężarowych
        Mechanik maszyn rolniczych
        Mechanik maszyn budowlanych
        Mechanik maszyn przemysłowych
        Blacharz
        Lakiernik samochodowy
        Lakiernik przemysłowy
        Laminiarz
        Kierowca C
        Kierowca C+E
        Kierowca autobusów D
        Kierowca pojazdów specjalistycznych
        Kierowca betoniarki
        Kierowca śmieciarki
        Kierowca wywrotki
        Operator dźwigu
        Operator dźwigu wieżowego
        Operator koparki
        Frezer konwencjonalny
        Frezer CNC
        Tokarz konwencjonalny
        Tokarz CNC
        Operator prasy krawędziowej konwencjonalnej / giętarki
        Operator lasera
        Operator prasy krawędziowej CNC / giętarki
        Operator wytłaczarki konwencjonalnej
        Operator wytłaczarki CNC
        Operator wtryskarki CNC
        Programista frezarek CNC
        Programista tokarek CNC
        Komisjoner
        Operator wózka widłowego
        Pracownik produkcji mięsa
        Pracownik produkcji ryb
        Pracownik produkcji przemysłowej
        Pracownik zbioru warzyw i owoców
        Ogrodnik
        Personel sprzątający
        Pomoc kuchenna
        Kelner
        Kucharz
        Chef de Partie
        Pastry Chef
        Chef de Cuisine
        Sous Chef
        Commis de Cuisine
        Pizza maker
        Recepcjonista
        Barman
        Piekarz
        Alpinista
        Opiekun osoby starszej
        Rolnik
        Pracownik ogólnobudowlany
        Pomocnik murarza
        Regipsiarz
        Pokojówka
        Magazynier
        Szlifierz
        Spawacz MAG
        Operator maszyn
        Tapicer
        Monter rusztowań
        Sanitariusz
        Monter ogrzewania
        Monter opon
        Polernik
        Recepcjonistka
        Animator czasu wolnego
        Lakiernik drewna
        Rzeźnik
        Portier nocny
        Logistyk
        Kurier
        Ślusarz maszynowy
        Kierownik zmiany
        Kierownik budowy
        Betoniarz
        Zbrojarz
        Dentysta
        Monter izolacji
        Sprzedawca
        Pracownik biurowy
        Kierowca B
        Pracownik myjni
        Pomocnik elektryka
        Górnik
        Piaskarz
        Operator ładowarki
        Przedstawiciel handlowy
        Dozorca
        Szwaczka
        Archiwista
        Odlewnik
        Formierz - odlewnik
        Kosmetyczka
        Pielęgniarz / Pielęgniarka
        Masażysta
        Operator maszyn tnących CNC
        Pracownik działu obsługi klienta
        Monter modułów fotowoltaicznych
        Hydraulik
        Pracownik wywozu śmieci
        Barista
        Hostessa
        Złota rączka
        Pomocnik hydraulika
        Stajenny
        Strażak
        Operator wtryskarki
        Monter mebli
        Cukiernik
        Wytapiacz
        Operator suwnicy
        Monter okien
        Elektryk przemysłowy - utrzymanie ruchu
        Marynarz
        Budowniczy statków
        Wulkanizator
        Florysta/ Florystka
        Lutowacz
        Mechanik okrętowy
        Pracownik produkcji
        Elektryk
        Elektronik
        Tynkarz
        Operator wyciągu narciarskiego
        Lakiernik proszkowy
        Pracownik robót ziemnych
        Operator zagęszczarki
        Monter drzwi
        Kręgarz
        Leśniczy
        Pracownik ochrony
        Mechatronik
        Elektryk samochodowy
        Blacharz samochodowy
        Pomocnik montera ogrzewania
        Mechanik Offshore
        Elektryk Offshore
        Kierownik zmiany offshore
        Menedżer HR
        Dyspozytor
        Monter rur
        Operator wózka widłowego wysokiego składu
        Pracownik pralni
        pomocnik dekarza
        Malarz przemysłowy
        Galwanizer
        Szklarz
        Farmaceuta
        Monter sztucznej trawy
        Drukarz
        Operator robota spawalniczego
        Pracownik restauracji
        Mechanik autobusów
        Pracownik budownictwa podziemnego
        Weterynarz
        Spawacz orbitalny
        Kierownik restauracji
        Zmywacz naczyń
        Monter
        Powder coater
        Plumber
</list>
    """.trimIndent()
}

data class ProfessionalExperiences(val experiences: List<ProfessionalExperienceWithSingleTask>)

data class ProfessionalExperienceWithSingleTask(
    val dateFrom: String = "",
    val dateTo: String = "",
    val country: String = "",
    val city: String = "",
    val company: String = "",
    val profession: String = "",
    val professionDe: String = "",
    val professionEng: String = "",
    val experienceTask: List<Task> = emptyList()
)
