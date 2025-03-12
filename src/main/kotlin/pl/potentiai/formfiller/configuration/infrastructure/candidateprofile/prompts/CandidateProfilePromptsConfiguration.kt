package pl.potentiai.formfiller.configuration.infrastructure.candidateprofile.prompts

import com.aallam.openai.client.OpenAI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.web.client.RestTemplate
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.common.*
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.origin.CandidateProfilePromptRunner
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.origin.ProfessionalExperiencePrompt
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.splitted.ExperienceDetailsPrompt
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.splitted.ExperienceLocationPrompt
import pl.potentiai.formfiller.infrastructure.ai.prompt.candidateprofile.splitted.SplittedCandidateProfilePromptRunner
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

@Configuration
class CandidateProfilePromptsConfiguration {

    @Bean
    fun restTemplate(): RestTemplate =
        RestTemplate()

    @Bean
    fun restContentReader(restTemplate: RestTemplate, gptAPIKey: String): RESTContentReader =
        RESTContentReader(restTemplate, gptAPIKey)

    @Bean
    fun educationPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): EducationPrompt =
        EducationPrompt(openAI, timeableWrapper)

    @Bean
    fun keySkills(openAI: OpenAI, timeableWrapper: TimeableWrapper): KeySkillsPrompt =
        KeySkillsPrompt(openAI, timeableWrapper)

    @Bean
    fun languagesPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): LanguagesPrompt =
        LanguagesPrompt(openAI, timeableWrapper)

    @Bean
    fun personalDetailsPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): PersonalDetailsPrompt =
        PersonalDetailsPrompt(openAI, timeableWrapper)

    @Bean
    fun professionalExperiencePrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): ProfessionalExperiencePrompt =
        ProfessionalExperiencePrompt(openAI, timeableWrapper)

    @Bean
    fun taskTranslatorPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): TasksTranslatorPrompt =
        TasksTranslatorPrompt(openAI, timeableWrapper)

    @Bean
    fun experienceLocationPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): ExperienceLocationPrompt =
        ExperienceLocationPrompt(openAI, timeableWrapper)

    @Bean
    fun experienceDetailsPrompt(openAI: OpenAI, timeableWrapper: TimeableWrapper): ExperienceDetailsPrompt =
        ExperienceDetailsPrompt(openAI, timeableWrapper)

    @Bean
    @Primary
    fun candidateProfilePromptRunner(
        personalDetailsPrompt: PersonalDetailsPrompt,
        languagesPrompt: LanguagesPrompt,
        keySkillsPrompt: KeySkillsPrompt,
        educationPrompt: EducationPrompt,
        professionalExperiencePrompt: ProfessionalExperiencePrompt,
        tasksTranslatorPrompt: TasksTranslatorPrompt,
    ): CandidateProfilePromptRunner =
        CandidateProfilePromptRunner(
            personalDetailsPrompt = personalDetailsPrompt,
            languagesPrompt = languagesPrompt,
            keySkillsPrompt = keySkillsPrompt,
            educationPrompt = educationPrompt,
            professionalExperiencePrompt = professionalExperiencePrompt,
            tasksTranslatorPrompt = tasksTranslatorPrompt
        )

    @Bean
    fun splittedCandidateProfilePromptRunner(
        personalDetailsPrompt: PersonalDetailsPrompt,
        languagesPrompt: LanguagesPrompt,
        educationPrompt: EducationPrompt,
        experienceLocationPrompt: ExperienceLocationPrompt,
        experienceDetailsPrompt: ExperienceDetailsPrompt,
        tasksTranslatorPrompt: TasksTranslatorPrompt,
    ): SplittedCandidateProfilePromptRunner =
        SplittedCandidateProfilePromptRunner(
            personalDetailsPrompt = personalDetailsPrompt,
            languagesPrompt = languagesPrompt,
            educationPrompt = educationPrompt,
            experienceLocationPrompt = experienceLocationPrompt,
            experienceDetailsPrompt = experienceDetailsPrompt,
            tasksTranslatorPrompt = tasksTranslatorPrompt
        )
}