package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.origin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import pl.potentiai.formfiller.domain.candidateprofile.model.KnowledgeOfLanguages
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common.*

interface ICandidateProfilePromptRunner {
    suspend fun run(rawCvContent: String): PromptResult
}

class CandidateProfilePromptRunner(
    private val personalDetailsPrompt: PersonalDetailsPrompt,
    private val languagesPrompt: LanguagesPrompt,
    private val keySkillsPrompt: KeySkillsPrompt,
    private val educationPrompt: EducationPrompt,
    private val professionalExperiencePrompt: ProfessionalExperiencePrompt,
    private val tasksTranslatorPrompt: TasksTranslatorPrompt
): ICandidateProfilePromptRunner {

    private val mapper = jacksonObjectMapper()

    override suspend fun run(rawCvContent: String): PromptResult = coroutineScope {
        val personalDetailsDeferred = async { personalDetailsPrompt.extractToObject(rawCvContent) }
        val languagesDeferred = async { languagesPrompt.extractToObject(rawCvContent) }
        val experienceDeferred = async { professionalExperiencePrompt.extractToObject(rawCvContent) }
        val educationDeferred = async { educationPrompt.extractToObject(rawCvContent) }

        val languages = languagesDeferred.await()
        val education = educationDeferred.await()
        val personalDetails = personalDetailsDeferred.await()
        val keySkills = KeySkills(emptyList())
        val experience = experienceDeferred.await()
        val tasks = experience.experiences.flatMap { it.experienceTask }
            .filter { it.name.isNotEmpty() }
        val translatedTasks = if (tasks.isEmpty()) Tasks(emptyList()) else tasksTranslatorPrompt.extractToObject(mapper.writeValueAsString(experience.experiences.flatMap { it.experienceTask }.distinct()))

        PromptResult(
            languages = languages,
            education = education,
            personalDetails = personalDetails,
            keySkills = keySkills,
            experience = experience,
            translatedTasks = translatedTasks
        )
    }
}

data class PromptResult(
    val languages: KnowledgeOfLanguages,
    val education: Educations,
    val personalDetails: CandidateProfileRest,
    val keySkills: KeySkills,
    val experience: ProfessionalExperiences,
    val translatedTasks: Tasks
)
