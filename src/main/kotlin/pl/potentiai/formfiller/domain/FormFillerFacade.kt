package pl.potentiai.formfiller.domain

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.web.multipart.MultipartFile
import pl.potentiai.formfiller.api.EmailDTO
import pl.potentiai.formfiller.api.EmailHelperRequest
import pl.potentiai.formfiller.domain.candidateprofile.CandidateProfileService
import pl.potentiai.formfiller.domain.candidateprofile.model.CandidateProfile
import pl.potentiai.formfiller.domain.candidateprofile.model.ProfessionalExperience
import pl.potentiai.formfiller.domain.common.ai.AIClient
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.splitted.ExperienceDetailsPrompt
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.splitted.ExperienceLocationPrompt
import pl.potentiai.formfiller.infrastructure.ai.prompt.mail.MailPromptRunner
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

class FormFillerFacade(
    private val candidateProfileService: CandidateProfileService,
    private val mailPromptRunner: MailPromptRunner,
    private val aiClient: AIClient,
    private val openAI: OpenAI,
    private val wrapper: TimeableWrapper,
) {
    suspend fun createCandidateProfile(file: MultipartFile): CandidateProfile =
        candidateProfileService.process(file)

    fun getStats(): Map<String, Any> =
        candidateProfileService.getStats()

    suspend fun prepareEmail(request: EmailHelperRequest): EmailDTO {
        return EmailDTO(content = mailPromptRunner.create(request)!!)
    }

    suspend fun translate(content: String): String =
        aiClient.translate(content)

    suspend fun test(): String {
        val prompt = ExperienceLocationPrompt(openAI, wrapper)
        val text = """""${'"'}
            Stanislaw Misch 
            born on 22 July 1966 in Grodzisk Wkp, Poland

            -1.09.1981-30.09.1984 - carpentry workshop - 'U Kromy' in Nowy Tomyśl - carpenter

            -15.02.1985-31.12.1988 - Construction and Installation Plant in Nowy Tomyśl - carpenter

            -27.02.1987-16.12.1988 - Obligatory Military Service

            -2.01.1989-30.09.1994 - Wielkopolskie Przedsiębiorstwo Produkcji Wikliniarskiej [Wicker Production Plant] 'Las' in Nowy Tomyśl - carpenter

            -1.10.1994-31.07.1999 - Zakład Tokarsstwa w Drewnie [Wood Turning Plant] - Nowy Tomyśl - carpenter - foreman

            -20.09.1999 -31.03.2000 - 'ATLAS' - Meble kuchenne SC [Kitchen Furniture] Nowy Tomyśl - carpenter

            -11.12.2000 -31.12.2000 - Jarosław Szubacarpentry workshop - Nowy Tomyśl - carpenter

            -1.10.2001 - 31.05.2006 - Usługowy Zakład Stolarski [Carpentry Services] - Roman Kroma - Nowy Tomyśl - carpenter

            -2.06.2006 - 10.12.2006 - 'DEREK' - Szczecin - work in Denmark - construction and renovation works, construction of houses

            -11.12.2006 - 25.07.2007 - 'LIVORA' - Projekt A/S - Nels Bohrs Vej 17 B - 8660 Skanderborg - work position: construction carpenter - construction of cottages

            -28.08.2007 - 22.02.2010 - 'OLE- B/EK-PEDERSEN Aps - TYLSTRUP E J 54- 9320 HAJLERUP - construction and renovation works

            -23.02.2010 - 01.10.2011 - Byg Aalborg APS - Solv. Skovvej 52 9000 Aalborg - construction and renovation works

            -8.05.2012 - 7.05.2013 - Persona Mobile - employment services - production and installation of plastic windows

            -8.05.2013 - 15.08.2017 - J + G MIHM - plastic window fitter

            -28.09.2017 - 31.12.2017 - RAINER.DK - finishing works

            -30.01.2018 - 8.05.2018 - LINIX.DK - roof constructions, finishing works

            -9.05.2019 - 20.12.2019 - Elinstalator Rune Jensen Aps - finishing works

            I am looking for a job position of carpenter in Denmark (Copenhagen)
            My contact phone number: +48 697 503 868
            ``` -27.01.2020-30.12.2020 - Bygenevgi Aps - general construction works  
            -22.03.2021-10.03.2022 - Vikar Invest II Aps - In the profession of a carpenter  
            -21.03.2022-15.04.2022 - AK Byg Aps - demolition, installation of floor panels  
            -25.04.2022-28.10.2022 - Vikar Invest Aps - plasterboard and flooring installer  
            -28.11.2022-22.12.2022 - Viktech - apartament renovations plasterboards installer  
            -17.04.2023-13.02.2024 - HOWICO - plasterboard and ceiling fitter trolltex  
            -15.04.2024-16.09.2024 - JCV Service Aps - in the profession of a carpenter  
            -23.09.2024-11.10.2024 - VITTCHEN - in the profession of a carpenter, floor Frome structures and panel assembly  

            My contact number: +48 697 503 868  
            E-mail address: stanislaw.misch@onet.pl
        """.trimIndent()

        val secondPrompt = ExperienceDetailsPrompt(openAI, wrapper)
        coroutineScope {
            val first = async { prompt.extractToObject(text) }
            val second = async { secondPrompt.extractToObject(text) }

            val firstResult = first.await()
            val secondResult = second.await()

            val firstPart = firstResult.experience.associateBy { it.index }
            val secondPart = secondResult.experiences.associateBy { it.index }

            val x =  firstPart.map {
                ProfessionalExperience(
                    dateFrom = it.value.dateFrom,
                    dateTo = it.value.dateTo,
                    country = it.value.country,
                    city = it.value.city,
                    company = secondPart[it.key]!!.companyName,
                    profession = secondPart[it.key]!!.profession,
                    professionDe = secondPart[it.key]!!.professionDe,
                    professionEng = secondPart[it.key]!!.professionEng,
                    experienceTaskDe = emptyList(),
                    experienceTaskEng = emptyList()
                )
            }

            println(x)
        }


        return "done"
    }
}

class FileProcessingError(message: String): RuntimeException(message)