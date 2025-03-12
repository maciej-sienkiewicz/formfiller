package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.splitted

import com.aallam.openai.client.OpenAI
import pl.potentiai.formfiller.domain.candidateprofile.model.Task
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common.Extractor
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

class ExperienceDetailsPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): Extractor<ProfessionalExperiencesTest>(openAI, ProfessionalExperiencesTest::class, timeableWrapper, "experience-details") {

    override fun model(): String = "gpt-4o"

            override val PROMPT = """
Z przesłanego dokumentu wyekstraktuj  informacje o KAŻDEJ JEDNEJ pozycji w doświadczeniu. Nie halucynuj.

{
    "experiences": [
        {
            "index": String,
            "companyName": String,
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
1. **index** - Pozycja CV w dokumencie.
2. **companyName** - Nazwa firmy lub forma/rodzaj zatrudnienia, której dotyczy pozycja. 
3. **"profession"**: Wartość MUSI być wzięta z listy przesłanej poniżej. Lista będzie oznaczona w tagach <list></list>. Dobierz tylko jedną najtrafniejszą wartość.
4. **professionDe"**: Wykonywany zawód dla tego okresu czasu - w języku niemieckim
5. **professionEng"**: Wykonywany zawód dla tego okresu czasu - w języku angielskim
6. **"experienceTask"**: Lista zadań opisanych w pracy, np. obowiązki, projekty. Dla każdego zadania utwórz osobny obiekt z polem "name".
7. Jeśli w dokumencie brakuje informacji, żeby uzupełnić któreś pole to ustaw pustą wartość.
8. W odpowiedzi nie dodawaj żadnych dodatkowych znaków: żadnych komentarzy, dodatkowych pól czy informacji.
9. Upewnij się, że pole **profession** nie jest puste i zostało wybrane z listy. 
Wstaw index dla każdej pozycji w doświadczeniu.

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

data class ProfessionalExperiencesTest(val experiences: List<ProfessionalExperienceWithSingleTaskTest>)

data class ProfessionalExperienceWithSingleTaskTest(
    val index: String = "",
    val companyName: String = "",
    val profession: String = "",
    val professionDe: String = "",
    val professionEng: String = "",
    val experienceTask: List<Task> = emptyList()
)
