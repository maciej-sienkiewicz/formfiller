package pl.potentiai.formfiller.api.model

import pl.potentiai.formfiller.domain.candidateprofile.model.CandidateProfile

data class PersonalDetailsDTO(
    val firstName: IsRequiredWrapper<String>,
    val lastName: IsRequiredWrapper<String>,
    val citizenship: IsRequiredWrapper<String>,
    val birthDate: IsRequiredWrapper<String>,
    val birthPlace: IsRequiredWrapper<String>,
    val email: IsRequiredWrapper<String>,
    val phoneCode: IsRequiredWrapper<String>,
    val phoneNumber: IsRequiredWrapper<String>,
    val building: IsRequiredWrapper<String>,
    val local: IsRequiredWrapper<String>,
    val street: IsRequiredWrapper<String>,
    val postalCode: IsRequiredWrapper<String>,
    val city: IsRequiredWrapper<String>
)

data class LanguageDTO(
    val name: String,
    val languageLevel: String
)

data class FlexibilityDTO(
    val driverLicence: IsRequiredWrapper<List<String>>
)

data class KeySkillDTO(
    val name: String
)

data class TaskDTO(val name: String)

data class ProfessionalExperienceDTO(
    val dateFrom: IsRequiredWrapper<String>,
    val dateTo: IsRequiredWrapper<String>,
    val country: IsRequiredWrapper<String>,
    val city: IsRequiredWrapper<String>,
    val company: IsRequiredWrapper<String>,
    val profession: IsRequiredWrapper<String>,
    val professionDe: IsRequiredWrapper<String>,
    val professionEng: IsRequiredWrapper<String>,
    val experienceTaskDe: IsRequiredWrapper<List<TaskDTO>>,
    val experienceTaskEng: IsRequiredWrapper<List<TaskDTO>>,
)

data class EducationDTO(
    val fromTo: String = "",
    val school: String = "",
    val profession: String = "",
    val typeOfSchoolEng: String = "",
    val typeOfSchoolDe: String = ""
)

data class CandidateProfileDTO(
    val personalInfo: PersonalDetailsDTO,
    val languages: IsRequiredWrapper<List<LanguageDTO>>,
    val flexibility: IsRequiredWrapper<FlexibilityDTO>,
    val keySkills: IsRequiredWrapper<List<KeySkillDTO>>,
    val education: IsRequiredWrapper<List<EducationDTO>>,
    val professionalExperience: IsRequiredWrapper<List<ProfessionalExperienceDTO>>
)

class IsRequiredWrapper<T>(
    val isRequired: Boolean,
    val value: T?
)

fun CandidateProfile.toDTO() : CandidateProfileDTO =
    CandidateProfileDTO(
        personalInfo = PersonalDetailsDTO(
            firstName = IsRequiredWrapper(
                isRequired = true,
                value = this.personalInfo.firstName
            ),
            lastName = IsRequiredWrapper(
                isRequired = true,
                value = this.personalInfo.lastName
            ),
            citizenship = IsRequiredWrapper(
                isRequired = true,
                value = this.personalInfo.citizenship
            ),
            birthDate = IsRequiredWrapper(
                isRequired = true,
                value = this.personalInfo.birthDate
            ),
            birthPlace = IsRequiredWrapper(
                isRequired = true,
                value = this.personalInfo.birthPlace
            ),
            email = IsRequiredWrapper(
                isRequired = true,
                value = this.personalInfo.email
            ),
            phoneCode = IsRequiredWrapper(
                isRequired = true,
                value = this.personalInfo.phoneCode
            ),
            phoneNumber = IsRequiredWrapper(
                isRequired = true,
                value = this.personalInfo.phoneNumber
            ),
            building = IsRequiredWrapper(
                isRequired = true,
                value = this.personalInfo.building
            ),
            local = IsRequiredWrapper(
                isRequired = false,
                value = this.personalInfo.local
            ),
            street = IsRequiredWrapper(
                isRequired = true,
                value = this.personalInfo.street
            ),
            postalCode = IsRequiredWrapper(
                isRequired = false,
                value = this.personalInfo.postalCode
            ),
            city = IsRequiredWrapper(
                isRequired = true,
                value = this.personalInfo.city
            ),),
        languages = IsRequiredWrapper(
            isRequired = true,
            value = this.languages.languages.map { LanguageDTO(it.name, it.languageLevel) }
        ),
        flexibility = IsRequiredWrapper(
            isRequired = true,
            value = FlexibilityDTO(
                driverLicence = IsRequiredWrapper(
                    isRequired = true,
                    value = this.flexibility.driverLicence
                ),),),
        keySkills = IsRequiredWrapper(
            isRequired = false,
            value = this.keySkills.map { KeySkillDTO(it.name) }
        ),
        education = IsRequiredWrapper(
            isRequired = false,
            value = this.education.map {
                EducationDTO(
                    fromTo = "${it.from} - ${it.to}",
                    school = it.school,
                    profession = it.profession,
                    typeOfSchoolEng = it.typeOfSchoolEng,
                    typeOfSchoolDe = it.typeOfSchoolDe
                )
            }
        ),
        professionalExperience = IsRequiredWrapper(
            isRequired = true,
            value = this.professionalExperience.map {
                ProfessionalExperienceDTO(
                    dateFrom = IsRequiredWrapper(
                        isRequired = true,
                        value = it.dateFrom
                    ),
                    dateTo = IsRequiredWrapper(
                        isRequired = true,
                        value = it.dateTo
                    ),
                    country = IsRequiredWrapper(
                        isRequired = true,
                        value = it.country
                    ),
                    city = IsRequiredWrapper(
                        isRequired = true,
                        value = it.city
                    ),
                    company = IsRequiredWrapper(
                        isRequired = true,
                        value = it.company
                    ),
                    profession = IsRequiredWrapper(
                        isRequired = true,
                        value = it.profession
                    ),
                    professionDe = IsRequiredWrapper(
                        isRequired = true,
                        value = it.professionDe
                    ),
                    professionEng = IsRequiredWrapper(
                        isRequired = true,
                        value = it.professionEng
                    ),
                    experienceTaskDe = IsRequiredWrapper(
                        isRequired = false,
                        value = it.experienceTaskDe.map { TaskDTO(it.name) }
                    ),
                    experienceTaskEng = IsRequiredWrapper(
                        isRequired = false,
                        value = it.experienceTaskEng.map { TaskDTO(it.name) }
                    ),
                    )
            }
        )
    )