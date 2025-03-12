package pl.potentiai.formfiller.infrastructure.ai.temporary

import pl.potentiai.formfiller.api.EmailHelperRequest
import pl.potentiai.formfiller.domain.candidateprofile.model.*
import pl.potentiai.formfiller.domain.common.ai.AIClient
import pl.potentiai.formfiller.domain.candidateprofile.enhancer.LanguagesEnhancer
import pl.potentiai.formfiller.domain.mail.EmailHelperResponse

class FakeAIClient: AIClient {
    override suspend fun fromRawTextToCandidateProfile(content: String): CandidateProfile {
        val profile = CandidateProfile(
            personalInfo = PersonalDetails(
                citizenship = "Polska",
                birthDate = "30-10-1997",
                birthPlace = "Trzcianka",
                email = "zaboobin@gmail.com",
                phoneCode = "+48",
                phoneNumber = "888915358",
                building = "Nie wiem",
                local = "nie wiem",
                street = "nie wiem",
                postalCode = "nie wiem",
                city = "nie wiem"
            ),
            languages = KnowledgeOfLanguages(
                listOf(
                    Language(
                        "Angielski",
                        "A1/A2"
                    ),
                    Language(
                        "Niemiecki",
                        "C1"
                    ),
                )
            ),
            flexibility = Flexibility(),
            keySkills = emptyList(),
            education = listOf(
                Education(
                    from = "2022",
                    to = "2023",
                    school = "Szkoła zawodowa",
                    profession = "Brukarz",
                    typeOfSchoolEng = "Eng",
                    typeOfSchoolDe = "DE"
                ),
                Education(
                    from = "2023",
                    to = "2024",
                    school = "Szkoła pawodowa",
                    profession = "Tynkarz2",
                    typeOfSchoolEng = "Eng2",
                    typeOfSchoolDe = "DE2"
                ),
            ),
            professionalExperience = listOf(
                ProfessionalExperience(
                    dateFrom = "2024-10-12",
                    dateTo = "2024-10-12",
                    profession = "Ślusarz",
                    country = "Polska",
                    city = "Krzyż",
                    company = "MerBUD",
                    experienceTaskDe = emptyList(),
                    experienceTaskEng = emptyList(),
                ),
                ProfessionalExperience(
                    dateFrom = "2023-02-11",
                    dateTo = "2023-03-12",
                    profession = "Brukarz",
                    country = "Polska",
                    city = "Krzyż",
                    company = "MerBUD",
                    experienceTaskDe = emptyList(),
                    experienceTaskEng = emptyList(),
                )
            )
        )
        return LanguagesEnhancer().enhance(profile)
    }

    override suspend fun prepareEmail(input: EmailHelperRequest): EmailHelperResponse {
        TODO("Not yet implemented")
    }

    override suspend fun translate(content: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun extractContent(base64: String): String {
        TODO("Not yet implemented")
    }
}