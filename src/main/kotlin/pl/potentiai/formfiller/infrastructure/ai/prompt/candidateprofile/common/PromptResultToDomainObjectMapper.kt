package pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common

import pl.potentiai.formfiller.domain.candidateprofile.model.CandidateProfile
import pl.potentiai.formfiller.domain.candidateprofile.model.KeySkill
import pl.potentiai.formfiller.domain.candidateprofile.model.ProfessionalExperience
import pl.potentiai.formfiller.domain.candidateprofile.model.Task
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.origin.PromptResult

object PromptResultToDomainObjectMapper {
    fun PromptResult.toDomainObject(): CandidateProfile {
        val translationMap = translatedTasks.translated.associateBy { it.original }

        return CandidateProfile(
            personalInfo = personalDetails.personalInfo,
            flexibility = personalDetails.flexibility,
            languages = languages,
            keySkills = keySkills.keySkills.map {
                KeySkill(
                    it
                )
            },
            education = education.educations,
            professionalExperience = experience.experiences.map {
                val translatedTasks = it.experienceTask.mapNotNull { translationMap.get(it.name) }
                ProfessionalExperience(
                    dateFrom = it.dateFrom,
                    dateTo = it.dateTo,
                    country = it.country,
                    city = it.city,
                    company = it.company,
                    profession = it.profession,
                    professionDe = it.professionDe,
                    professionEng = it.professionEng,
                    experienceTaskDe = translatedTasks.map {
                        Task(
                            it.experienceTaskDe.replaceFirstChar { it.uppercaseChar() }
                        )
                    }.toList(),
                    experienceTaskEng = translatedTasks.map {
                        Task(
                            it.experienceTaskEng.replaceFirstChar { it.uppercaseChar() }
                        )
                    }.toList(),
                )
            }
        )
    }
}