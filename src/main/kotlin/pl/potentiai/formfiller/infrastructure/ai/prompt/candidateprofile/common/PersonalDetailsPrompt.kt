package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common

import com.aallam.openai.client.OpenAI
import pl.potentiai.formfiller.domain.candidateprofile.model.Education
import pl.potentiai.formfiller.domain.candidateprofile.model.Flexibility
import pl.potentiai.formfiller.domain.candidateprofile.model.PersonalDetails
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

class PersonalDetailsPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): Extractor<CandidateProfileRest>(openAI, CandidateProfileRest::class, timeableWrapper, "personalDetails") {
    override val PROMPT: String = """
Twoim zadaniem jest przeanalizowanie tekstu wejściowego i wyodrębnienie informacji o osobie w poniższej strukturze JSON:

{
  "personalInfo": {
    "firstName": "String",
    "lastName": "String",
    "citizenship": "String",
    "birthDate": "String", -- format YYYY-MM-DD
    "birthPlace": "String",
    "email": "String",
    "phoneCode": "String",
    "phoneNumber": "String",
    "building": "String",
    "local": "String",
    "street": "String",
    "postalCode": "String",
    "city": "String"
  },
  "flexibility": {
    "driverLicence": ["String"], -- lista stringów. dostępne wartości: D, A, B, C, T, C+E, B+E
  }
}

Zasady:
1. Nie wnoś żadnych informacji spoza tekstu wejściowego.
2. Jeśli którejkolwiek informacji nie można znaleźć w tekście, przypisz wartość `"UNDEFINED"`.
3. **"firstName" i "lastName"**: Wyciągnij imię i nazwisko osoby.
4. **"citizenship"**: Wyciągnij obywatelstwo, jeśli jest podane w tekście.
5. **"birthDate"**: Data urodzenia w formacie YYYY-MM-DD.
6. **"birthPlace"**: Miejsce urodzenia osoby.
7. **"email"**: Wyciągnij adres e-mail z tekstu.
8. **"phoneCode" i "phoneNumber"**: Jeśli numer telefonu jest podany, rozdziel kod kraju (np. +48) od numeru telefonu. Jeśli brak numeru, przypisz "UNDEFINED".
9. **"building", "local", "street", "postalCode", "city"**: Wyciągnij informacje adresowe:
   - "street" nie może zawierać numeru budynku ani mieszkania.
   - "building" to numer budynku.
   - "local" to numer mieszkania, jeśli jest dostępny.
10. **"driverLicence"**: Jeśli osoba ma prawo jazdy, dopasuj i wpisz wartości: "D", "A", "B", "C", "T", "C+E", "B+E". Jeśli brak informacji, wstaw pustą listę.

Nie dodawaj żadnych dodatkowych informacji, opisów ani znaków poza oczekiwanym JSON.
""".trimIndent()

    val PROFESSIONS = """
        Zapamiętaj tą listę zawodów i wykorzystaj ją w następnych poleceniach:
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
        Praca niezwiązana z wykonywanym zawodem
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
    """.trimIndent()
}

data class CandidateProfileRest(
    val personalInfo: PersonalDetails = PersonalDetails(),
    val flexibility: Flexibility = Flexibility(),
    val education: List<Education> = emptyList(),
)