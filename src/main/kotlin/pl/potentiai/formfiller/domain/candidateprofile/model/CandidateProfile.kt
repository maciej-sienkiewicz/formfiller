package pl.potentiai.formfiller.domain.candidateprofile.model

data class PersonalDetails(
    val firstName: String = "",
    val lastName: String = "",
    val citizenship: String = "",
    val birthDate: String = "",
    val birthPlace: String = "",
    val email: String = "",
    val phoneCode: String = "",
    val phoneNumber: String = "",
    val building: String = "",
    val local: String = "",
    val street: String = "",
    val postalCode: String = "",
    val city: String = ""
)

data class KnowledgeOfLanguages(
    val languages: List<Language> = emptyList()
)

data class Language(
    val name: String = "",
    val languageLevel: String = ""
)

data class Flexibility(
    val driverLicence: List<String> = emptyList(),
)

data class KeySkill(
    val name: String = ""
)

data class Task(val name: String = "")

data class Education(
    val from: String = "",
    val to: String = "",
    val school: String = "",
    val profession: String = "",
    val typeOfSchoolEng: String = "",
    val typeOfSchoolDe: String = ""
)

data class ProfessionalExperience(
    val dateFrom: String = "",
    val dateTo: String = "",
    val country: String = "",
    val city: String = "",
    val company: String = "",
    val profession: String = "",
    val professionDe: String = "",
    val professionEng: String = "",
    val experienceTaskDe: List<Task> = emptyList(),
    val experienceTaskEng: List<Task> = emptyList(),
)

data class CandidateProfile(
    val personalInfo: PersonalDetails = PersonalDetails(),
    val languages: KnowledgeOfLanguages = KnowledgeOfLanguages(),
    val flexibility: Flexibility = Flexibility(),
    val keySkills: List<KeySkill> = emptyList(),
    val education: List<Education> = emptyList(),
    val professionalExperience: List<ProfessionalExperience> = emptyList()
)