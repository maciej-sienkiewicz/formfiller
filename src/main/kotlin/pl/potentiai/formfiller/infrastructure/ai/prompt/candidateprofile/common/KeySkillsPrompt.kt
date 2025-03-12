package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common

import com.aallam.openai.client.OpenAI
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

class KeySkillsPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): Extractor<KeySkills>(openAI, KeySkills::class, timeableWrapper, "keyskills") {

    override val PROMPT = """
Twoim zadaniem jest przeanalizowanie tekstu wejściowego i wyodrębnienie umiejętności z poniższej listy. Należy znaleźć wyłącznie te elementy, które dokładnie odpowiadają wartościom na liście – z uwzględnieniem wielkości liter, znaków diakrytycznych i formatowania. Wynik powinien być przedstawiony wyłącznie w formie JSON:

{
    "keySkills": ["umiejętność_1", "umiejętność_2", "..."]
}

Zasady:
1. Przeszukuj tekst i wybierz tylko te wartości, które dokładnie odpowiadają którejkolwiek pozycji z podanej listy.
2. Nie zmieniaj formatu, wielkości liter ani nie dodawaj wartości, które nie występują na liście.
3. Jeśli w tekście nie znajdzie się żadna umiejętność z listy, zwróć pustą tablicę w polu "keySkills".
4. Nie dodawaj żadnych opisów, wyjaśnień ani znaków poza oczekiwanym JSON.
        Lista:
        spawanie ręczne gazowe
        przecinanie ręczne tlenowe
        lutowanie gazowe
        cięcie ręczne przecinarką częściowo zmechanizowaną
        spawanie ręczne łukowe elektrodą otuloną
        spawanie elektrodą topliwą w osłonie gazów aktywnych - metodą
        spawanie automatyczne
        obsługa robota spawalniczego
        spawanie półautomatyczne
        spawanie elektrodą topliwą w osłonie gazów obojętnych -
        spawanie elektrodą nietopliwą w osłonie argonu - metodą TIG
        spawanie laserowe
        spawanie plazmowe
        spawanie na podstawie rysunku technicznego
        spawanie na podstawie rysunku izometrycznego
        szlifowanie szlifierką kątową
        szlifowanie szlifierką palcową
        szlifowanie szlifierką stołową
        wiercenie otworów w metalu
        przygotowanie elementów stalowych – gięcie
        prostowanie płomieniowe konstrukcji stalowych
        samodzielne wykonywanie prac zgodnie z dokumentacją
        wizualne sprawdzanie na bieżąco jakości wykonanych złączy
        usuwanie wad i niezgodności powstałych przy spawaniu
        przygotowanie powierzchni do spawania, które polega na
        sprawdzanie jakości wykonywanych prac spawalniczych na
        posługiwanie się elektronarzędziami
        samodzielne składanie konstrukcji stalowych
        montaż wind
        montaż wind osobowo-towarowych
        montaż dźwigów dla osób niepełnosprawnych
        montaż dźwigów (wind) panoramicznych
        regulacja wind
        samodzielny montaż części elektrycznej wind
        samodzielny montaż części mechanicznej wind
        montaż innych urządzeń dźwigowych
        naprawa systemów sterowania windami
        regulacja przeciwwagi w windach
        regulacja mechanizmów drzwi w windach
        podłączanie przewodów elektrycznych do paneli sterowania wind
        nadzorowanie pracowników
        spawanie zbiorników paliwowych
        spawanie stali drobnoziarnistej
        spawanie wielowarstwowe
        spawanie kontenerów
        spawanie konstrukcji stalowych
        spawanie elementów ogrodzeniowych (balustrad, krat)
        spawanie elementów transformatorów
        spawanie maszyn rolniczych
        spawanie przyczep
        spawanie części samochodów
        spawanie precyzyjne bardzo małych elementów
        spawanie dużych elementów
        cięcie termiczne blach
        maszynowe cięcie blach i kształtowników
        spawanie stali czarnej
        spawanie stali nierdzewnej
        spawanie aluminium
        PA – pozycja podolna
        PB – pozycja naboczna
        PC – pozycja naścienna
        PD – pozycja okapowa
        PE – pozycja pułapowa
        PF – spawanie blach w pozycji pionowej z dołu do góry
        PG – spawanie blach w pozycji pionowej z góry do dołu
        PH- spawanie rur w pozycji pionowej z dołu do góry
        PJ – spawanie rur w pozycji pionowej z góry do dołu
        H-L045 – spawanie rur w pozycji pochylonej pod kątem 45° z
        J-L045 – spawanie rur w pozycji pochylonej pod kątem 45° z
        &lt; 1 mm
        1-2 mm
        3-5 mm
        &gt; 5 mm
        badanie nieniszczące złączy spawanych (NDT,&nbsp;ang.&nbsp;non-destructive
        badanie wizualne złączy spawanych (VT)
        badanie penetracyjne złączy spawanych (PT)
        badanie magnetyczno-proszkowe (MT) - nieniszczące spawów
        badanie radiologiczne złączy spawanych (RT)
        badanie ultradźwiękowe złączy spawanych (UT)
        badanie szczelności złączy spawanych (LT)
        badanie prądami wirowymi złączy spawanych (ET)
        badanie niszczące złączy spawanych (DT,&nbsp;ang.&nbsp;destructive
        badanie mechaniczne złączy spawanych
        przeprowadzania prób technologicznych w zakresie procesów
        badanie metalograficzne (badania makroskopowe, mikroskopowe)
        zgrzewanie (spawanie) elektryczne oporowe&nbsp;(rezystancyjne)
        zgrzewanie tworzyw sztucznych za pomocą zgrzewarki na gorące
        zgrzewanie zgniotowe
        zgrzewanie wybuchowe
        zgrzewanie tarciowe
        spawanie (zgrzewanie) indukcyjne
        spawanie dyfuzyjne
        zgrzewanie zwarciowe
        zgrzewanie doczołowe iskrowe
        zgrzewanie prądami wielkiej częstotliwości
        zgrzewanie gorącą płytą
        zgrzewanie wibracyjne
        montaż instalacji elektrycznych w obiektach przemysłowych (m.
        montaż instalacji elektrycznych w obiektach użyteczności
        montaż instalacji elektrycznych w branży petrochemicznej
        montaż instalacji elektrycznych w obiektach
        montaż instalacji elektrycznych na farmach wiatrowych
        montaż instalacji elektrycznych na statkach
        montaż instalacji elektrycznych w obiektach mieszkalnych
        montaż tras kablowych i układanie i łączenie przewodów
        podłączanie urządzeń elektrycznych wewnątrz budynków
        montaż instalacji alarmowych
        montaż instalacji przeciwpożarowych
        instalacje niskiego napięcia
        instalacje średniego napięcia
        instalacje wysokiego napięcia
        budowa szaf sterowniczych (rozdzielni elektrycznych) zgodnie
        naprawa rozdzielni energii elektrycznej
        okablowywanie szaf sterowniczych
        podłączenie czujników systemów zabezpieczeń
        wykonywanie przeglądów technicznych, konserwacji oraz napraw
        wykonywanie przeglądów technicznych, konserwacji oraz napraw
        przeprowadzanie konserwacji oraz napraw układów automatyki
        instalacja sieci komputerowych
        wykonywanie pomiarów, prób po montażu i naprawie instalacji,
        montowanie i konserwowanie zabezpieczeń przeciwzwarciowych,
        budowa szaf sterowniczych (rozdzielni elektrycznych) zgodnie
        przeprowadzanie kontroli urządzeń elektrycznych stosowanych
        planowanie i nadzorowanie sieci elektroenergetycznej
        montaż ochrony odgromowej obiektów budowlanych
        montaż oświetlenia drogowego
        budowa linii napowietrznych
        wykonywanie prac związanych z utrzymaniem ruchu
        dokonywanie przeglądu i prac remontowych linii
        kontrola i utrzymanie niezbędnego zapasu części zamiennych
        codzienna kontrola urządzeń kontrolno-pomiarowych
        usuwanie awarii maszyn
        programowanie sterowników PLC
        doświadczenia z systemem Modbus
        doświadczenie z systemem Gyrolok (system łączenia rur)
        doświadczenie z systemem Swagelok (system łączenia rur)
        doświadczenie z systemem A-lok (system łączenia rur)
        doświadczenie z rurami Monol (Alloy 400)
        SEP - posiadanie polskich uprawnień elektrycznych
        NEN3140 - certyfikat potwierdzający znajomość&nbsp;norm
        DSB - norweski certyfikat umożliwiający podjęcie pracy jako
        GWO (Holandia) - podstawowe szkolenie w zakresie
        ATEX (Holandia) - certyfikat bezpieczeństwa
        montaż i naprawa instalacji wodociągowych,
        montaż i naprawa
        montaż hydroforów i uzdatniaczy wody
        montaż podgrzewaczy wody
        budowa i montaż oczyszczalni ścieków
        montaż instalacji grzewczej na gaz płynny
        uzbrajanie terenu w sieć wodno-kanalizacyjną
        montaż zbiorników na szambo
        montaż systemów deszczowych
        montaż systemów instalacyjnych w budynkach i obiektach
        utrzymanie prawidłowego funkcjonowania sieci rur oraz
        dokonywanie przeglądu sieci rur
        obsługa zgrzewarki elektrooporowej
        spawanie gazowe
        spawanie elementów stalowych
        montaż brodzików oraz kabin prysznicowych
        uszczelnianie instalacji
        montaż oraz podłączenie&nbsp;zraszaczy
        montaż oraz łączenie rur
        praca na wysokościach
        systemy grzewcze
        układanie rur gazowych
        montaż kominków
        montaż wkładów kominkowych
        montaż nagrzewnic gazowych
        montaż kotłów grzewczych
        montaż systemów ogrzewania podłogowego
        montaż systemów ogrzewania nadmuchowego
        przeglądy urządzeń systemów grzewczych
        czyszczenie instalacji grzewczych
        lutowanie oraz łączenie rur
        gwintowanie oraz szlifowanie rur
        uszczelnianie złącz
        lutowanie
        spawanie instalacji grzewczych
        zgrzewanie rur
        wyznaczanie trasy przechodzenia instalacji
        czytanie rysunku technicznego
        montaż pomp ciepła
        montaż ogrzewania grawitacyjnego
        montaż instalacji centralnego ogrzewania w oparciu o pompy
        systemy chłodnicze
        budowa oraz serwis komór chłodniczych
        montaż agregatów
        montaż oraz serwis mebli chłodniczych
        montaż klimatyzatorów
        montaż inteligentnych systemów klimatyzacyjnych (VRV)
        montaż wewnętrznych systemów wentylacyjnych
        montaż zewnętrznych systemów wentylacyjnych
        naprawy i konserwacje urządzeń chłodniczych&nbsp;
        montaż sprężarek/agregatów do urządzeń chłodniczych
        czytanie rysunków technicznych i dokumentacji projektowej
        usuwanie awarii chłodniczych i klimatyzacyjnych
        doradztwo techniczne
        projektowanie agregatów chłodniczych i szaf sterowniczych
        obsługa elektronarzędzi (wiertarka, szlifierka itd.)
        wykonywanie i obróbka elementów meblowych
        montaż i demontaż mebli
        naprawianie i renowacja mebli
        konserwacja i lakierowanie mebli
        kontrolowanie jakości materiałów i wykonanych mebli
        dobór odpowiedniego gatunku drewna do konkretnego rodzaju
        wycinanie i łączenie elementów mebli, oklejanie fornirem lub
        szlifowanie, heblowanie i lakierowanie powierzchni
        wykonywanie, naprawa i demontaż elementów drewnianych
        wykonywanie, naprawianie i demontowanie ścianek działowych
        wykonywanie, naprawianie i demontowanie sufitów
        produkcja, montaż i naprawa schodów
        montaż i naprawa stolarki okiennej i drzwiowej
        wykonywanie i remontowanie podłóg, schodów, boazerii i
        wykonywanie oraz montowanie ścianek działowych oraz sufitów
        wykonywanie drewnianych obiektów tymczasowych na placu
        obsługa maszyn i narzędzi stolarskich
        prace na linii montażowej stolarki drewnianej (okuwanie,
        przygotowanie elementów stolarki drewnianej do lakierowania,
        praca przy produkcji okien i drzwi
        pakowanie elementów wyprodukowanych
        wykonywanie konstrukcji drewnianych
        montaż, demontaż i naprawa więźby dachowej
        wykonywanie, demontaż i naprawa zadaszeń
        montaż wiechy na szczycie konstrukcji dachowych
        formowanie i wykonywanie szalunków w konstrukcjach
        zalewanie szalunków betonem
        montaż gotowych komponentów ze stali lub drewna
        doświadczenie z systemami szalunkowymi Peri i Doka
        naprawa uszkodzonych elementów statku
        montaż ścianek działowych i sufitów podwieszanych wewnątrz
        montowanie drzwi i okien w kajutach
        montaż mebli i wyposażenia wewnątrz kajut
        obijanie blachą elementów drewnianych, montowanie rynien i
        montaż, demontaż i naprawa rynien,
        wykonywanie konserwacji i naprawy pokryć dachowych oraz
        wykonywanie pokryć dachowych różnymi materiałami
        wykonywanie izolacji dachów i stropodachów
        wykonywanie podkładów pod pokrycia dachowe
        wykonywanie prefabrykatów ścian i podłóg
        przygotowanie betonu do produkcji
        betonowanie szalunków i wykańczanie
        wykonywanie elementów betonowych lub żelbetonowych do
        wykonywanie elementów żelbetonowych, dróg, mostów, budowli
        montaż zbrojenia
        przycinanie i wyginanie prętów stalowch
        zalewanie konstrukcji betonem
        wykonywanie murów nośnych
        wykonywanie ścian działowych
        przygotowywanie zaprawy murarskiej
        wykonywanie fundamentów, murów zbrojonych, nadproży, stropów
        osadzanie parapetów
        usuwanie ubytków w tynku
        wykonanie podkładów pod tynki szlachetne
        montaż okien i drzwi
        licowanie ścian cegłą licową, okładzinami ceramicznymi,
        wykonywanie izolacji akustycznych i termicznych
        wykonywanie izolacji przeciwwilgociowych, paroszczalnych i
        wykonywanie posadzek bezspoinowych z materiałów
        wykonywanie posadzek z żywic i materiałów bitumicznych
        montaż instalacji sprężonego powietrza
        wykonywanie pomocniczych robót murarskich
        murowanie cegłą klinkierową
        wykonywanie tynków zewnętrznych i wewnętrznych
        malowanie ścian oraz sufitów
        malowanie elewacji budynków
        malowanie okien, drzwi, schodów oraz innych konstrukcji
        wykonywanie powłok malarskich elementów i konstrukcji
        wykonywanie ścian działowych i okładzin ściennych
        wykonywanie sufitów podwieszanych i okładzin sufitowych
        montaż sztukaterii
        wykonywanie zabudów poddaszy i obudów dachów
        wykonywanie suchych podkładów podłogowych i podłóg
        wykonywanie elementów aranżacji wnętrz z wykorzystaniem
        wykonywanie remontów, konserwacji i naprawy elementów suchej
        lakierowanie podłóg
        cyklinowanie podłóg drewnianych
        przygotowanie powierzchni do położenia kafelek
        układanie płytek
        wykonywanie elementów podłoża
        montaż płyt kartonowo-gipsowych
        wykonywanie ozdobnych mozaik
        dokładne fugowanie kafli podłogowych i ściennych
        biały montaż (montaż bidetów, kabin prysznicowych,
        wykonywanie posadzek betonowych
        układanie paneli podłogowych drewnianych
        układanie kostki brukowej i krawężników
        przycinanie elementów z kamienia
        montaż płyt fasadowych
        montaż izolowanych płyt warstwowych
        montaż izolacji na dachach płaskich oraz hydroizolacji
        układanie kostki brukowej
        montaż, konserwowanie oraz renowacja kamienia naturalnego
        montaż, konserwowanie oraz renowacja kamienia sztucznego
        wykonywanie detali architektonicznych z kamienia
        obróbka ręczna i mechaniczna kamienia
        obsługa maszyn do rozcinania bloków skalnych na płyty
        wykonywanie prac przy konserwacji zabytków
        wykonywanie powierzchni brukarskiej o podbudowy drogowej
        wykonywanie robót towarzyszących pracom brukarskim oraz
        wykonywanie prac brukarskich zgodnie z rysunkiem
        rozbiórka ulic oraz dróg
        niwelowanie terenu
        układanie nawierzchni drogi wg ustalonego wzoru
        układanie nawierzchni chodników z klinkieru
        układanie nawierzchni chodników z kamienia
        układanie nawierzchni chodników z bloków
        montaż fasad (prefabrykowanych elementów zewnętrznych)
        montaż szkła na elewacji budynku
        montaż kamienia elewacyjnego
        obsługiwanie urządzeń kontrolno-pomiarowych
        obsługa urządzeń diagnostycznych
        ustalanie przyczyn awarii pojazdu samochodowego
        naprawa silników
        naprawa układu napędowego
        naprawa oraz konserwacja układu hamulcowego
        naprawa układu kierowniczego
        naprawa zawieszenia pojazdów samochodowych
        wymiana olejów i płynów
        naprawa pojazdów wg standardów ASO
        naprawa pojazdów zabytkowych
        renowacja pojazdów zabytkowych
        przeprowadzanie przeglądów okresowych samochodów
        ocena stopnia zużycia części
        ocena zakresu awarii pojazdu
        wymiana opon
        wyważanie kół
        sprawdzanie geometrii pojazdu
        kompleksowa naprawa elektroniki samochodowej
        wymiana rozrządu
        przygotowanie powierzchni do lakierowania
        przygotowywanie pojazdów do napraw nadwozia
        dobieranie koloru lakieru do karoserii
        wygładzanie karoserii pojazdu z chropowatości przy użyciu
        gruntowanie przed lakierowaniem
        polerowanie powierzchni auta
        lakierowanie przy pomocy pistoletów pneumatycznych
        lakierowanie karoserii samochodów
        lakierowanie zadrapań i wgnieceń
        wykonywanie operacji obróbki ręcznej i mechanicznej blach
        naprawy karoserii
        konserwacja karoserii przed korozją
        lakierowanie maszyn
        lakierowanie elementów metalowych metodą proszkową
        lakierowanie rur i obiektów przemysłowych
        lakierowanie konstrukcji stalowych
        kontrola jakości oraz nadzór procesu lakierowania
        naprawy oraz konserwacje lakierowanych powierzchni
        lakierowanie urządzeń przemysłowych metodą natryskową
        obsługa urządzeń lakierniczych
        polerowanie po utwardzeniu lakieru
        laminowanie, formowanie oraz szlifowanie elementów z
        mieszanie i nakładanie żelkotu
        ręczne nakładanie żelkotu
        laminowanie skrzydeł dla elektrowni wiatrowych
        szlifowanie i obróbka laminatu
        przygotowanie form do natrysku żelkotu
        praca przy produkcji wyrobów z laminatu
        montaż elementów z laminatu
        produkcja i regeneracja form kadłubowych łodzi i jachtów
        laminowanie różnych części łodzi i jachtów
        laminowanie za pomocą pompy próżniowej i wałków do
        laminowanie ręczne
        sprawdzanie pojazdu i części pojazdu pod kątem
        usuwanie drobnych awarii w pojeździe
        przyjmowanie zleceń załadunku od dysponenta
        określanie tras przejazdu
        pomoc przy rozładunku i załadunku pojazdu
        wskazanie towaru do załadowania
        bezpieczne i pewne kierowanie pojazdem
        przestrzeganie czasu prowadzenia pojazdu, przerw oraz
        usuwanie drobnych usterek
        prowadzenie księgi przejazdów
        załatwianie formalności przy przekraczaniu granicy
        doświadczenie w prowadzeniu autobusów
        bezpieczny i punktualny transport pasażerów
        udzielanie informacji pasażerom o różnych sprawach
        przeprowadzanie drobnych napraw
        doradzanie pasażerom autobusu
        planowanie personelu
        prowadzenie autobusu wycieczkowego
        prowadzenie autobusu dalekobieżnego
        prowadzenie autobusu liniowego
        prace konserwacyjne
        sprzedaż biletów
        sprawdzanie stanu technicznego pojazdu
        jazda autobusem zgodnie z przepisami ruchu drogowego
        przeprowadzenie kontroli biletów
        prowadzenie śmieciarki
        pomoc przy rozładunku i załadunku odpadów
        sprawdzanie zabezpieczenia ładunku
        doświadczenie z ciągnikiem siodłowym
        transfer kontenerów na śmieci do śmieciarki
        zbieranie worków
        prowadzenie 2 do 4-osiowej wywrotki
        działanie w celu najlepszej obsługi klienta
        eksploatacja żurawi wieżowych
        konserwacja dźwigów
        wykonywanie różnych pobocznych prac na budowie
        transport materiałów budowlanych oraz prefabrykatów
        prace na dźwigu
        bezpieczne parkowanie
        transport dużych i ciężkich ładunków
        sprawdzanie otoczenia pracy przed każdym transportem
        sprawdzanie stanu technicznego dźwigu
        sprawdzanie stanu hamulców
        przestrzeganie stabilności oraz nośności pojazdu
        weryfikacja trasy transportu, miejsca załadunku i
        zachowanie bezpiecznego odstępu
        montaż części
        programowanie maszyn CNC z pulpitu
        wywoływanie programu
        programowanie maszyny CNC
        programowanie tokarki CNC
        programowanie frezarki CNC
        programowanie prasy krawędziowej CNC
        liczba osi: 2
        liczba osi: 3
        ręczne wprowadzanie danych
        obróbka częsci metalowych
        kontrola jakości produktu
        usuwanie błędów
        gwintowanie
        czytanie rysunku technicznego
        znajomość czytania rysunku technicznego
        obróbka i organizacja danych komputerowych
        planowanie procesów pracy
        programowanie 5-osiowej symultanicznej frezarki
        zbrojenie i obsługa centr pięcioosiowych (CNC)
        wykonywanie różnych czynności przy produkcji części
        ustawianie punktu zero w obrabiarkach CNC
        ustawianie odpowiednich narzędzi
        załadowanie odpowiedniego programu do maszyny CNC
        kontrola obróbki metalu oraz przepływu płynu chłodniczego
        pomiar gotowych produktów suwmiarką
        optymalizacja sterowania maszyny
        doświadczenie ze sterowaniem Siemens
        znajomość sterowania Heidenhain
        doświadczenie ze sterowaniem Sinumeric 840D
        kontrola narzędzi
        szczegółowa kontrola jakości gotowych produktów
        obsługa i ustawianie maszyn Trumpf
        obsługa i ustawianie maszyn Siemens
        obsługa i ustawianie maszyn Fanuc
        ustawianie oraz obsługa frezarek CNC ze sterowaniem
        załadunek i rozładunek maszyn
        ustawianie i korygowanie parametrów producji
        nadzór procesów produkcyjnych
        usuwanie ewentualnych usterek
        samodzielne przeprowadzanie prac konserwacyjnych oraz
        wczytywanie programu do maszyny
        obróbka metalu
        obsługa tokarek
        planowanie produkcji komponentów
        produkcja części pojedynczych i seryjnych
        spawanie pojedynczych komponentów
        produkcja seryjna komponentów
        toczenie
        frezowanie
        toczenie konwencjonalne
        frezowanie konwecjonalne
        obsługa narzędzi mierniczych
        komisjonowanie i sortowanie towarów
        komisjonowanie za pomocą skanera (pick-by-voice)
        składowanie palet z zaopatrzeniem
        pakowanie
        zbieranie i usuwanie matariału do pakowania
        przygotowywanie artykułów do wysyłki
        drukowanie etykiet do wysyłek
        wprowadzanie odbioru towarów do systemu
        opracowywanie zleceń klientów
        opracowywanie zleceń klientów w programie SAP
        kontrola przyjmowania i wydawania towarów w magazynie
        realizowanie przyjęcia i odbioru towarów
        ogólne pracy magazynowe
        załadunek przygotowanych towarów wózkami podnośnikowymi
        obsługa ręcznego wózka paletowego
        obsługa elektrycznego wózka paletowego
        obsługa niskopodłogowego wózka paletowego
        obsługa wysokopodłogowego wózka paletowego
        obsługa wózka widłowego
        obsługa wózka wysokiego składowania
        obsługa wózka widłowego z wysuwanym masztem
        obsługa wózka widłowego bocznego załadunku
        uprawnienia na wózek widłowy
        produkcja różnego rodzaju wyrobów mięsnych
        pomoc w uboju
        porcjowanie mięsa
        magazynowanie i kontrola żywności zgodnie ze standardami
        sortowanie wyrobów mięsnych
        przetwarzanie i sortowanie ryb
        przetwarzanie i sortowanie owoców morza i ryb
        ręczne obrabianie ryb nożem
        odważanie wg wskazań wagowych
        praca na lini produkcyjnej
        obsługa maszyn produkcyjnych
        czyszczenie maszyn produkcyjnych
        obsługa maszyn pakujących
        odbiór dostaw i zamówień
        pakowanie i komisjonowanie produktów
        kompletowanie zamówień (komisjonowanie) za pomocą skanera
        etykietowanie towarów
        przygtotowywanie produktów do wysłyki
        wprowadzanie danych do systemu komputerowego
        ogólne prace pomocniczne
        prace porządkowe w przestrzeni produkcyjnej
        załadowywanie i rozładowywanie cięzarówek i kontenerów
        prace przy zbieraniu warzyw i owoców
        sortowanie, czyszczenie i pakowanie
        samodzielne wykonywanie prac ogrodniczych i
        pielęgnacja krajobrazu i ogólne prace ogrodnicze
        pielęgnacja trawy (koszenie, nawożenie, podlewanie,
        pielęgnacja roślin (sadzenie, nawożenie, przesadzanie,
        przycinanie krzewów i drzew
        usuwanie chwastów
        pielęgnacja ścieżek i placy
        pielęgnacja zagajników i drzew
        usuwanie liści
        obsługa maszyn ogrodniczych (kosiarka żyłkowa, elektryczna,
        ochrona rośliności
        projektowanie i rewitalizacja ogrodów
        troska o czystośc i porządek na terenach zielonych
        sprzątanie i czyszczenie pokoi gości
        ścielenie łóżek i zmiana pościeli
        sprzątanie łazienek, zmiana ręczników, uzupełnianie
        prace porządkowe we wspólnej przestrzeni hotelowej (schody,
        wykonywanie prac sprzątających w pralni hotelowej
        pomoc przy prasowaniu i maglowaniu
        pomoc przy przygotowywaniu potraw, sałatek i deserów
        prace porządkowe w obrębie kuchni, zmywaka oraz pomieszczeń
        pomoc przy wydawaniu potraw i napojów
        przestrzeganie i wdrażanie standardów higieny (HACCP)
        przyjmowanie zamówień i przekazywanie zamówień kuchni
        dbanie o porządek na sali, stolikach, o zastawę i
        serwis dań i napojów
        rozliczanie zamówień złożonych przez gości
        odpowiedzialność za utrzymanie najwyższych standardów
        przygotowywanie potraw à-la-Carte
        mise en place - przygotowanie składników pożywienia do
        samodzielna praca na stanowisku kuchennym
        przygotowywanie i kreatywne podawanie zimnych i ciepłych dań
        instruowanie nowych pracowników i praktykantów na temat
        zamawianie produktów i kontrola jakości
        kontrola zapasów produktów spożywczych
        wymyślanie nowych i kreatywnych przepisów
        samodzielne prowadzenie kuchni
        przygotowywanie potraw regionalnych i międzynarodowych
        pomaganie szefowi kuchni w prowadzeniu kuchni
        przygotowywanie potraw wg ustalonych przepisów i
        planowanie budżetu
        produkcja ciast, tortów i innych słodkich wypieków
        formowanie wypieków
        wypiekanie chlebów, bułek i innych wypieków
        przygotowywanie potraw mięsnych
        przygotowywanie potraw rybnych
        przygotowywanie makaronów
        przygotowywanie potraw wegetariańskich
        przygotowywanie zup i sosów
        przygotowywanie sałatek
        przygotowywanie przystawek
        check in i check out gości
        opieka nad gośćmi, pomoc w rozwiązywaniu wszelkich
        podstawowe prace administracyjne
        przyjmowanie i opracowywanie rezerwacji
        prowadzenie korespondecji
        obsługa gości na barze
        wydawanie napoi pracownikom serwisu
        przygotowywanie ciepłych i zimnych napoi
        przygtotowywanie drinków
        mycie okien (uprawnienia IRATA)
        profesjonalne mycie okien
        profesjonalne mycie fasad
        czyszczenie różnych powierzchni pod ciśnieniem
        kotwienie rusztowań
        psychosocjalne wsparcie w codzienności
        pomoc przy wstawaniu i kładzeniu się do łóżka
        pomoc przy kąpielach oraz myciu się
        pomoc przy chodzeniu do toalety
        pomoc w ubieraniu i rozbieraniu
        pomoc w czesaniu i goleniu
        ogólna pomoc ruchowa pacjentowi
        pomoc w przyjmowaniu pokarmów
        pomoc w podawaniu leków
        zmiana opatrunków
        przygotowywanie posiłków
        towarzyszenie w wizytach lekarskich
        prowadzenie gospodarstwa domowego
        prowadzenie dokumentacji dot. opieki
        współpraca z członkami rodziny, lekarzami i terapeutami
        obsługa wózka przedniego
        prowadzenie własnej firmy, niezwiązanej z wykonywanym
        mycie i czyszczenie pojazdów
        tapetowanie ścian
        budowa murów oporowych
        budowa dróg oraz autostrad
        montaż rusztowań
        szpachlowanie sufitów i ścian
        tynkowanie
        konserwacja i naprawa maszyn budowlanych
        ochrona osób i mienia
        szlifowanie elementów ze stali nierdzewnej
        szlifowanie elementów ze stali czarnej
        inwentaryzacja towarów
        wystawianie dokumentów magazynowych
        praca w chłodni
        kompletacja zamówień w oparciu o system słuchawkowy
        konserwacja i naprawa maszyn rolniczych
        operator maszyn
        wykonywanie prac ślusarskich
        montaż konstrukcji stalowych
        obsługa pras
        piaskowanie
        prace restauratorskie
        prace rozbiórkowe
        sprzedaż
        obsługa klienta
        obróbka drewna
        spawanie elektrodą
        cięcie manualne balch
        obsługa stołu do cięcia szkła
        szklenie
        montaż luster
        obróbka szkła
        spawanie tworzyw sztucznych EPDM
        tapicerowanie
        naprawa trakcji kolejowych
        obsługa pras krawędziowych CNC
        obsługa obrabiarek
        montaż ogrzewania olejowego
        montaż ogrzewania gazowego
        praca przy produkcji części samochodowych
        prowadzenie zajęć językowych z j. angielskiego
        animacja czasu wolnego
        nauka jeździectwa
        planowanie i organizacja programów rozrywkowych
        opieka nad dziećmi
        zarządzanie zespołem ludzi
        znajomość pakietu MS Office
        kompleksowa opieka nad gośćmi
        praca w recepcji hotelowej
        kontrolowanie / pilnowanie hotelu
        wymiana rur
        wymiana instalacji sanitarnych
        prowadzenie spotkań biznesowych
        utrzymywanie dobrych relacji z klientami
        wdrażanie projektów
        ewidencjonowanie towarów
        koordynowanie pracy magazynu
        tworzenie raportów
        praca na płycie lotniska przy obsłudze przylotów i wylotów
        ekspedycja (wysyłka) towarów
        dbałość o zaopatrzenie sklepu internetowego
        obsługa suwnicy
        spawanie grzejników
        utrzymywanie bębna w czystości
        prace biurowe
        spawanie miedzi
        zgrzewanie rur z tworzyw sztucznych PPR
        negocjowanie kontraktów
        obsługa maszyn wydobywczych
        przygotowanie złoża do eksploatacji
        zabezpieczenie szybów konstrukcjami stalowymi
        wydobycie węgla
        obsługa koparko-ładowarki
        dozór budynku
        tapicerowanie mebli
        obsługa archiwum
        zarządzanie dokumentami
        przygotowywanie surowca odlewniczego
        obróbka form
        wykonywanie odlewów
        obsługa pieców odlewniczych
        formowanie ręczne (wykonywanie odlewów i przygotowywanie
        zalewanie form
        obróbka odlewów
        obsługa maszyn wysokociśnieniowych
        zmywanie naczyń
        podawanie insuliny
        monitorowanie poziomu glukozy we krwii
        przeprowadzanie ćwiczeń gimnastycznych dla osób leżących
        opieka nad osobą leżącą
        fizjoterapia
        praca z pacjentami po udarze mózgu
        praca z osobami umierającymi
        opieka nad pacjentami poruszającymi się na wózku
        opieka nad osobami z chorobą Alzheimera oraz demencją
        opieka nad osobami chorymi na raka
        opieka nad pacjentami z cewnikiem
        opieka nad pacjentami chorymi na cukrzycę
        stosowanie sond
        obsługa wtryskarek
        obsługa wulkanizacyjnej prasy hydraulicznej
        laminowanie powierzchni żelaznych
        naprawa i uruchamianie wtryskarek
        obsługa żurawia wieżowego
        programowanie frezarek CNC ze sterowaniem Heidenhain
        programowanie frezarki CNC ze sterowaniem Haas
        Sinumerik 840D
        programowanie frezarki CNC ze sterowaniem Fanuc
        programowanie frezarki CNC ze sterowaniem Sinumerik
        programowanie tokarki CNC ze sterowaniem Haas
        programowanie tokarki CNC ze sterowaniem Sinumerik
        wykonywanie prac związanych z inżynierią ziemną, drogową i
        wykonywanie prac ziemnych
        wykonywanie prac hydraulicznych
        montaż rur przepustowych oraz falochronów
        Samodzielna praca z serwisowymi systemami informatycznymi
        Samodzielna praca z serwisowymi systemami informatycznymi
        Montaż i instalacja elementów elektrycznych pojazdów
        Montaż różnych elementów pojazdu w pojeździe, a następnie
        wykonanie niezbędnych przeróbek na całym pojeździe
        rozwiązywanie problemów i przeprowadzanie diagnostyki
        Wyszukiwanie usterek i naprawa systemów technicznych
        Instalacja specjalnego wyposażenia
        demontaż starych okien i drzwi
        montaż okien drewnianych
        montaż okien plastikowych (PCV)
        montaż okien aluminiowych
        montaż okuć metalowych
        osadzanie ościeżnic okiennych
        osadzanie ościeżnic drzwiowych
        regulacja drzwi oraz okien
        wykonywanie drobnych prac tynkarskich przy montażu okien
        wymiana szyb w oknach
        konserwacja i naprawa starych okien i drzwi
        montaż żaluzji
        montaż markiz
        montaż lub wymiana zamków
        przygotowanie ścian poprzez zdrapywanie, użycie papieru
        kładzenie tapety typu raufaza
        kładzenie tapety ze wzorem
        kładzenie tapety z włókna szklanego
        przygotowanie powierzchni poprzez ich szlifowanie i
        montaż balustrad
        przyjmowanie brudnej odzieży
        sortowanie odzieży według rodzaju tkaniny, koloru i
        załadunek i rozładunek pralek
        prasowanie ubrań
        składanie ubrań
        pakowanie ubrań do odbioru lub dostawy do klienta
        załadunek i rozładunek suszarek do ubrań
        produkcja balustrad
        montaż schodów metalowych
        produkcja schodów metalowych
        produkcja profili do okien
        praca przy produkcji elementów metalowych wykorzystywanych w
        szlifowanie powierzchni metalowych
        wykonywanie i naprawa elementów maszyn, urządzeń i
        konserwacja elementów maszyn, urządzeń i narzędzi
        wykonywanie połączeń montażowych
        kucie
        wyżarzanie
        skręcanie śrubunków
        ręczna i mechaniczna obróbka rur
        montaż systemów rurociągowych
        wykonywanie prób ciśnieniowych systemów rurociągowych
        konserwacja oraz naprawa systemów rurociągowych
        łączenie rur wg systemu Swagelok
        łączenie rur wg systemu Gyrolok
        łączenie rur wg systemu A-lok
        montaż i naprawa instalacji wodociągowych
        naprawa rur w instalacji centralnego ogrzewania
        wykonywanie prefabrykowanych elementów rurociągowych
        praca zgodnie z rysunkiem technicznym
        praca zgodnie z rysunkiem izometrycznym rurociągów
        budowa sieci ciepłowniczych
        budowa sieci gazowych
        budowa sieci wysokociśnieniowych
        montaż zasuw, filtrów, pomp, urządzeń pomiarowych
        cięcie tkanin obiciowych
        zszywanie części tkanin
        renowacja i produkcja wszelkiego rodzaju mebli
        pokrywanie elementów tapicerki
        montaż nóżek i kółek
        klejenie oraz zszywanie akcesoriów do mebli
        cięcie i mocowanie pianek do detali
        obróbka końcowa poszyć, a także ich wykańczanie i łączenie
        mocowanie oczek i zatrzasków do obrabianych przedmiotów
        montaż elementów mechanicznych lub elektronicznych w meblach
        obróbka i dopasowanie skór
        malowanie natryskowe agregatem hydrodynamicznym (airless)
        montaż karniszy
        samodzielne wykonywanie mebli w oparciu o własny projekt
        tworzenie indywidualnych mebli lub ich części pod zamówienie
        prace modyfikacyjne przy meblach (przeróbki mebli)
        wykonywanie, obróbka, okuwanie elementów meblowych
        produkcja i naprawa połączeń drewnianych
        renowacja mebli antycznych
        produkcja mebli kuchennych
        produkcja mebli biurowych
        produkcja mebli szkolnych
        produkcja mebli restauracyjnych
        produkcja mebli sklepowych
        produkcja mebli hotelowych
        montaż podkonstrukcji aluminiowej pod instalacje
        montaż i okablowanie modułów fotowoltaicznych różnych
        montaż falowników i zasobników energii (bez połączenia
        montaż naziemnych modułów fotowoltaicznych
        postawienie rusztowania przed rozpoczęciem montażu dla domów
        układanie kabli elektrycznych od wejścia dachowego do
        podłączenie falownika i integracja systemów magazynowania
        montaż systemów ładowania elektrycznego (skrzynki naścienne
        przeprowadzanie diagnostyki błędów i rozwiązywanie problemów
        wymiana elementów mechanicznych i elektrycznych (moduły
        instalacja i konfiguracja systemów monitoringu elektrowni
        podłączanie rozdzielni średniego napięcia
        montaż kanałów kablowych
        przygotowanie kabli i złączy
        okablowanie i wyposażenie szaf sterowniczych
        sprawdzenie jakości wykonania szaf sterowniczych, wykrywanie
        pomiary i testowanie instalacji i zespołów elektrycznych
        podłączenie szaf rozdzielczych i sterowniczych na miejscu
        podłączanie i regulacja czujników
        weryfikacja momentów obrotowych szaf sterowniczych w
        montaż urządzeń pomiarowych, zasilaczy, transformatorów
        wykonywanie pracy zgodnie z rysunkiem i planem
        wykonywanie instalacji elektrycznych w starym budownictwie
        wykonywanie instalacji elektrycznych w nowym budownictwie
        wykonywanie instalacji elektrycznych w domach
        montaż instalacji elektrycznych w obiektach użyteczności
        montaż instalacji elektrycznych w obiektach
        renowacja starych instalacji elektrycznych
        montaż oświetlenia wewnątrz budynków oraz na zewnątrz
        wykuwanie otworów pod rozprowadzenie kabli
        wiercenie otworów pod puszki elektryczne
        wykonywanie otworów w ścianach i sufitach
        budowa tras kablowych
        układanie i podłączanie kabli elektrycznych
        układanie przewodów elektrycznych
        montaż włączników oraz gniazdek
        montaż instalacji przeciwpożarowej
        montaż domowych systemów komunikacji
        podłączanie rozdzielnic
        czytanie schematów połączeń
        zapewnienie funkcjonalności systemów, wskazywanie błędów i
        wykonywanie przeglądów technicznych, przeglądów i napraw
        uruchomienie i optymalizacja działania systemów i maszyn
        współpraca przy ciągłym doskonaleniu urządzeń
        wykonywanie pomiarów elektrycznych i testów bezpieczeństwa
        tworzenie dokumentacji i raportów
        samodzielne programowanie sterowników PLC (niem. SPS) –
        zapewnienie dostępności części zamiennych w magazynie
        testowanie funkcji, bezpieczeństwa pracy i efektywności
        montaż, podłączanie i konserwacja instalacji
        czytanie i rozumienie rysunków instalacyjnych na statku,
        regularne testowanie sprzętu za pomocą woltomierzy i
        montaż kamer i monitorów na pokładach statków lub jachtów
        montaż instalacji pompowej na statkach lub jachtach
        montaż oświetlenia na pokładach statków lub jachtów
        montaż systemów sterowania silnikiem na pokładach statków
        montaż systemów elektrycznych drzwi na pokładach statków lub
        instalacja systemów mapowania i systemów satelitarnych na
        montaż różnych systemów bezpieczeństwa na pokładach statków
        montaż urządzeń radiowych na pokładach statków lub jachtów
        montaż i demontaż części pojazdu
        naprawa lub wymiana części karoserii
        prostowanie, dopasowanie i regulacja wymiarów szczelin w
        naprawy powypadkowe samochodów osobowych i dostawczych
        tuning optyczny auta, m.in. np. poszerzenie nadkoli,
        odnawianie maski, błotników, tylnego panelu oraz przodu
        umieszczanie pojazdu na ramie do prostowania
        renowacja lub wymiana oszklenia pojazdu
        szpachlowanie, szlifowanie części samochodowych
        prace wykończeniowe na obrabianych częściach karoserii
        wykonywanie tzw. Smart Repair
        polerowanie lakieru samochodowego
        klejenie części z plastiku
        wygładzanie części karoserii szlifierką oscylacyjną i
        wyposażanie i przerabianie pojazdów mechanicznych w
        przebudowy i nadbudowy samochodów
        tworzenie samochodów specjalnego przeznaczenia (np.
        restaurowanie tzw. klasyków
        oklejanie (zabezpieczanie), odbezpieczanie, czyszczenie, a
        szlifowanie oraz gruntowanie niedoskonałości
        nakładanie, rozmazywanie i wypełnianie środkiem
        mieszanie farb
        pełna lub częściowa naprawa lakiernicza wraz z
        nakładanie różnych warstw lakieru na wszystkie rodzaje
        dokładność w przygotowaniu pojazdu do lakierowania
        nakładanie powłoki ceramicznej na lakier samochodowy
        przygotowanie powierzchni, w tym czyszczenie, szpachlowanie,
        wykonywanie prac antykorozyjnych
        zabezpieczanie nielakierowanych części na podstawie rysunków
        mieszanie lakieru za pomocą maszyn do mieszania kolorów
        lakierowanie elementów przemysłowych z metalu
        lakierowanie urządzeń przemysłowych metodą natryskową
        lakierowanie pistoletem wysokociśnieniowym
        lakierowanie pistoletem niskoociśnieniowym
        Lakierowanie metodą Airmix
        lakierowanie konstrukcji stalowych
        lakierowanie rur
        lakierowanie elementów metalowych metodą proszkową
        polerowanie po utwardzeniu lakieru
        kontrola jakości procesu lakierowania
        czyszczenie i konserwacja lakierni
        przeprowadzanie przeglądów i prac konserwacyjnych maszyn
        diagnostyka błędów i usterek w układach mechanicznych,
        analiza uszkodzeń, prace naprawcze i regulacyjne na układach
        modernizacje i częściowe instalacje systemów
        prace serwisowe
        regulacja i sprawdzanie funkcji maszyn budowlanych za pomocą
        realizacja pomocy drogowej
        wyposażenie i przebudowa pojazdów i maszyn wraz z osprzętem
        wymiana olejów silnikowych, smarów i chłodziw
        montaż napędów (przekładnie ręczne, sprzęgła itp.)
        samodzielna praca według instrukcji i rysunków
        obróbka metalu oraz inne prace ślusarskie
        samodzielne układanie i fugowanie płytek zgodnie z
        układanie mozaiki
        licowanie ścian cegłą licową, okładzinami ceramicznymi,
        przygotowanie podłoży, ścian i podłóg np. do malowania,
        tynkowanie
        docinanie płytek
        silikonowanie w ramach białego montażu
        zakładanie hydroizolacji
        montaż instalacji podtynkowej
        montaż kabin prysznicowych, wanien i bidetów
        tworzenie niezbędnych warstw izolacyjnych i barierowych,
        obliczanie ilości potrzebnego materiału i planowanie
        wycinanie oraz wiercenie otworów w płytkach
        montaż systemów wentylacyjnych, kanałów i rur
        wytwarzanie, modyfikacja i montaż konstrukcji
        konserwacja, montaż i naprawa systemów wentylacji i
        mocowanie kanałów wentylacyjnych do ścian i sufitów
        uszczelnianie systemu wentylacji
        wymiana i odnowienie części zużywających się
        montaż modułów wentylacyjnych takich jak kanały
        samodzielna regulacja ilości powietrza
        ręczne i maszynowe formowanie blach, rur i profili
        konserwacja klap przeciwpożarowych
        czyszczenie i serwis systemu wentylacji
        lokalizowanie i usuwanie błędów oraz prace konserwacyjne
        prace przy instalacjach chłodniczych, szafach chłodniczych i
        wymiana części urządzeń chłodniczych taki jak silniki,
        czyszczenie układów (skraplaczy)
        montaż, przebudowa, modernizacja i optymalizacja systemów
        układanie przewodów chłodniczych
        montaż sprężarek i agregatów do urządzeń chłodniczych
        montaż i uruchomienie systemów klimatyzacji typu split
        przeprowadzanie konserwacji i napraw układów klimatyzacji
        samodzielne wykonanie mniejszych instalacji elektrycznych
        układanie i podłączanie kabli i przewodów środka
        wykonywanie prac remontowo-konserwacyjnych i czyszczenie
        naprawy podzespołów klimatyzacji
        montaż ogrzewania olejowego
        montaż ogrzewania gazowego
        montaż najnowszych rozwiązań energetycznych, takich jak
        instalowanie zbiornika na gaz płynny
        układanie rur instalacyjnych zgodnie z rysunkiem
        montaż nagrzewnic gazowych
        montaż i konfiguracja gazowych kotłów grzewczych
        montaż i konfiguracja olejowych kotłów grzewczych
        montaż ogrzewania podłogowego
        montaż grzejników
        montaż i podłączanie kominków
        montaż i kalibracja wymiany liczników ciepła i wody oraz
        wymiana kotłów
        budowa i orurowanie stacji uzdatniania wody
        montaż instalacji wodno-kanalizacyjnych
        docinanie rur
        zaprasowywanie (zaciskanie) rur miedzianych
        lutowanie rur miedzianych na twardo
        lutowanie rur miedzianych na miękko
        spawanie rur z czarnej stali
        łączenie rur ze stali nierdzewnej metodą zaciskania
        zgrzewanie rur z tworzyw sztucznych
        łączenie rur z tworzyw sztucznych wg systemu Rehau
        łączenie rur pex (instalacje podłogowe)
        obsługa zgrzewarki elektrooporowej
        układanie izolacji rur
        założenie kompletnej instalacji wodno-kanalizacyjnej w
        programowanie robota spawalniczego
        spawanie balustrad
        spawanie schodów metalowych
        spawanie zbiorników
        spawanie naczep
        spawanie kratownic
        spawanie ogrodzeń
        spawanie komponentów do różnych maszyn
        spawanie skrzynek i innych pojemników otwartych
        spawanie regałów
        spawanie profili elewacyjnych
        spawanie elementów pomp
        spawanie silosów
        spawanie ram pojazdów
        spawanie kątowników
        spawanie katalizatorów samochodowych
        spawanie platform
        spawanie różnego rodzaju wsporników
        spawanie elementów dekoracyjnych
        spawanie grzejników
        spawanie rur o niewielkich średnicach
        spawanie rur o dużych średnicach
        spawanie cienkich blach
        spawanie bardzo cienkich blach
        spawanie grubych blach
        spawanie stali kwasoodpornej
        spawanie stali kotłowej (żaroodpornej)
        spawanie żeliwa
        spawanie mosiądzu
        spawanie miedzi
        spawanie brązu
        spawanie stali ocynkowanej
        złącza doczołowe
        złącza teowe
        złącza kątowe
        złącza krzyżowe
        złącza pachwinowe
        złącza równoległe
        złącza nakładkowe
        złącza grzbietowe
        spoiny doczołowe
        spoiny pachwinowe
        PA – pozycja podolna
        PB – pozycja naboczna
        PC – pozycja naścienna
        PD – pozycja okapowa
        PE – pozycja pułapowa
        PF – spawanie blach w pozycji pionowej z dołu do góry
        PG – spawanie blach w pozycji pionowej z góry do dołu
        PH – spawanie rur w pozycji pionowej z dołu do góry
        PJ – spawanie rur w pozycji pionowej z góry do dołu
        H-L045 – spawanie rur w pozycji pochylonej pod kątem 45° z
        J-L045 – spawanie rur w pozycji pochylonej pod kątem 45° z
        przygotowanie powierzchni do spawania
        obsługa wózka widłowego na gas
        obsługa wózka widłowego (benzyna / diesel)
        obsługa elektrycznego wózka widłowego
        obsługa wózków widłowych (na gas, elektryczne, benzynowe,
        obsługa wózka widłowego czołowego (przedniego)
        obsługa wózka widłowego bocznego załadunku
        załadunek i rozładunek towarów wózkiem wysokiego
        obsługa wózka wysokiego składowania bocznego załadunku do 8
        obsługa wózka wysokiego składowania bocznego załadunku do
        obsługa wózka wysokiego składowania bocznego załadunku do
        obsługa wysokopodłogowego wózka paletowego
        obsługa niskopodłogowego wózka paletowego
        obsługa ręcznego wózka paletowego
        obsługa elektrycznego wózka paletowego
        pakowanie towaru
        pomoc przy rozładunku i załadunku pojazdu
        transport dużych i ciężkich ładunków
        kontrola jakości oraz wizualna kontrola towaru
        ogólne prace na magazynie
        duże doświadczenie w pracy magazynowej
        wymiana butli gazowych w wózkach widłowych
        znajomość systemów sanitarnych Sanipex
        znajomość systemów sanitarnych Alupex
        znajomość systemów sanitarnych Roth
        znajomość systemów sanitarnych Geberit
        znajomość systemów sanitarnych Meplo
        znajomość systemów sanitarnych Vavin
        znajomość systemów sanitarnych Uponor
        liczba osi: 4
        znajomość sterowania Mazatrol
        obsługa tokarek ze sterowaniem Mazatrol
        obsługa tokarek Mazak
        obsługa lasera CNC Bystronic
        obsługa tokarek Okuma
        obsługa frezarek DMU
        wiązanie prętów zbrojeniowych
        uzupełnianie dokumentacji przewozowej
        podłączanie naczep
        podłączanie przyczep
        obsługa koparki
        jazda pojazdem z manualną skrzynią biegów
        jazda pojazdem z automatyczną skrzynią biegów
        ważenie towarów
        obsluga minikoparki
        obsługa ładowarki kołowej
        obsługa kasy fiskalnej
        zapobieganie chorobom (szczepienia)
        leczenie chorób
        doradztwo dietetyczne
        spawanie orbitalne
        prowadzenie i sterowanie koparkami kołowymi
        prowadzenie i sterowanie koparkami gąsienicowymi
        prowadzenie i sterowanie mini koparkami
        wykonywanie prac ziemnych, takich jak wykopywanie wykopów,
        równanie i niwelowanie terenu pod projekty budowlane
        usuwanie gleby i skał oraz ładowanie urobku na ciężarówki
        przeprowadzanie codziennych inspekcji i konserwacji
        wykopy pod szamba i studnie
        ocena stopnia uszkodzeń spowodowanych wypadkami, korozją
        usuwanie uszkodzonych części karoserii, takich jak drzwi,
        naprawa karoserii przy użyciu specjalistycznych narzędzi
        wymiana uszkodzonych części
        używanie spawarek do łączenia lub naprawy elementów
        wygładzanie i przygotowanie powierzchni do malowania,
        przygotowanie do procesu malowania
        wyrównywanie i dopasowywanie części karoserii
        Experience with flat roofs
        Experience with pitched roofs
        Quality control and packaging of parts
        Powder coating of metal parts according to specifications
        Manually spray
        Mechanically spray
        Removing dirt, oil, rust using methods such as
        Smoothing imperfections on the surface to ensure an even
        Mixing different powders or adding additives to achieve the
        Adjusting the settings on the electrostatic spray gun
        Using an electrostatic spray gun to apply the powder evenly
        Applying multiple layers of powder to achieve the desired
        Checking for uniformity, smoothness, color accuracy, and
        Measuring the thickness of the coating to ensure it meets
        piaskowanie dużych konstrukcji stalowych
        tworzenie gładkiej powierzchni poprzez usuwanie
        dobór odpowiedniego rodzaju materiału ściernego w
        konfiguracja sprzętu do piaskowania, w tym zbiornika do
        kontrola powierzchni po piaskowaniu w celu zapewnienia
        piaskowanie
        piaskowanie kulkami szklanymi
        piaskowanie sodą
        piaskowanie stalowym śrutem
        piaskowanie piaskiem krzemionkowym
        piaskowanie na mokro
        piaskowanie powietrzem
        piaskowanie z użyciem tworzyw sztucznych
        montaż rur
        czytanie rysunków izometrycznych
        mierzenie rozmiarów rur i cięcie ich na wymiar
        łączenie rur przy użyciu spawania, lutowania, klejenia,
        instalacja zaworów, złączek, kołnierzy i innych elementów
        przeprowadzanie prób ciśnieniowych i innych kontroli w celu
        używanie manometrów, prób hydrostatycznych lub ciśnienia
        praca z maszyną do gięcia
        doświadczenie w instalacji, demontażu, wymianie i
        doświadczenie w wierceniu, gięciu, szlifowaniu
        budowa złożonych systemów rurowych
        instalacja, montaż, spawanie i utrzymanie systemów rurowych
        praca z systemami rurowymi z czarnej stali
        systemy rurowe z żeliwa
        systemy rurowe z miedzi
        systemy rurowe z PVC
        systemy rurowe z PE
        systemy rurowe z aluminium
        rutynowa konserwacja, taka jak czyszczenie i inspekcja
        Installing and connecting central heating systems to the
        Pulling and connecting cables
        Connecting cables to instruments, engines and steering
        Mounting cables through cable glands and connecting them
        Building and wiring new cabinets from scratch
        Mounting copper, metalparts and components
        Labeling cabels in new cabinets
        Mounting and connecting steering components on plates for
        Connecting small cabinets on the bridge, engine room and
        Connecting cables in the steering control room
        gięcie rur
        szlifowanie
        wiercenie
        gwintowanie
        lutowanie twarde
        spawanie rur
        demontaż rur
        montaż połączeń kołnierzowych
    ""${'"'}.trimIndent()

    val PROFESSIONS = ""${'"'}
        Zapamiętaj tą listę zawodów. W następnej wiadomości otrzymasz listę instrukcji.
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

data class KeySkills(val keySkills: List<String>)