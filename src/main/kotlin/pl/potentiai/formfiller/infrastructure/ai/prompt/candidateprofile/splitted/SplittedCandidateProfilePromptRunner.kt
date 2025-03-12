package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.splitted

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common.*
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.origin.*

class SplittedCandidateProfilePromptRunner(
    private val personalDetailsPrompt: PersonalDetailsPrompt,
    private val languagesPrompt: LanguagesPrompt,
    private val educationPrompt: EducationPrompt,
    private val experienceDetailsPrompt: ExperienceDetailsPrompt,
    private val experienceLocationPrompt: ExperienceLocationPrompt,
    private val tasksTranslatorPrompt: TasksTranslatorPrompt
): ICandidateProfilePromptRunner {

    private val mapper = jacksonObjectMapper()

    override suspend fun run(rawCvContent: String): PromptResult = coroutineScope {
        val personalDetailsDeferred = async { personalDetailsPrompt.extractToObject(rawCvContent) }
        val languagesDeferred = async { languagesPrompt.extractToObject(rawCvContent) }
        val educationDeferred = async { educationPrompt.extractToObject(rawCvContent) }
        val experienceDetailsDeferred =  async { experienceDetailsPrompt.extractToObject(rawCvContent) }
        val experienceLocationDeferred =  async { experienceLocationPrompt.extractToObject(rawCvContent) }

        val languages = languagesDeferred.await()
        val education = educationDeferred.await()
        val personalDetails = personalDetailsDeferred.await()
        val keySkills = KeySkills(emptyList())
        val experienceDetails = experienceDetailsDeferred.await()
        val experienceLocation = experienceLocationDeferred.await()

        val firstPart = experienceLocation.experience.associateBy { it.index }
        val secondPart = experienceDetails.experiences.associateBy { it.index }

        val experience: List<ProfessionalExperienceWithSingleTask> =  firstPart.map {
            ProfessionalExperienceWithSingleTask(
                dateFrom = it.value.dateFrom,
                dateTo = it.value.dateTo,
                country = it.value.country,
                city = it.value.city,
                company = secondPart[it.key]!!.companyName,
                profession = secondPart[it.key]!!.profession,
                professionDe = secondPart[it.key]!!.professionDe,
                professionEng = secondPart[it.key]!!.professionEng,
                experienceTask = secondPart[it.key]!!.experienceTask,
            )
        }

        val tasks = experienceDetails.experiences.flatMap { it.experienceTask }.toSet()
        val translatedTasks = if (tasks.isEmpty()) Tasks(emptyList()) else tasksTranslatorPrompt.extractToObject(mapper.writeValueAsString(experienceDetails.experiences.flatMap { it.experienceTask }.distinct()))

        PromptResult(
            languages = languages,
            education = education,
            personalDetails = personalDetails,
            keySkills = keySkills,
            experience = ProfessionalExperiences(experience),
            translatedTasks = translatedTasks
        )
    }
}