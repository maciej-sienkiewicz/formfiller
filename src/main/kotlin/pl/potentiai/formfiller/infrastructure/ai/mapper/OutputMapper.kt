package pl.potentiai.formfiller.infrastructure.ai.mapper

import pl.potentiai.formfiller.domain.candidateprofile.model.*


fun CandidateProfile.toCleanDomainObject(): CandidateProfile =
    CandidateProfile(
        personalInfo = PersonalDetails(
            firstName = emptyWhenUnknown(this.personalInfo.firstName),
            lastName = emptyWhenUnknown(this.personalInfo.lastName),
            citizenship = emptyWhenUnknown(this.personalInfo.citizenship),
            birthDate = emptyWhenUnknown(this.personalInfo.birthDate),
            birthPlace = emptyWhenUnknown(this.personalInfo.birthPlace),
            email = emptyWhenUnknown(this.personalInfo.email),
            phoneCode = emptyWhenUnknown(this.personalInfo.phoneCode),
            phoneNumber = emptyWhenUnknown(this.personalInfo.phoneNumber),
            building = emptyWhenUnknown(this.personalInfo.building),
            local = emptyWhenUnknown(this.personalInfo.local),
            street = emptyWhenUnknown(this.personalInfo.street),
            postalCode = emptyWhenUnknown(this.personalInfo.postalCode),
            city = emptyWhenUnknown(this.personalInfo.city)
        ),
        languages = KnowledgeOfLanguages(
            languages = this.languages.languages.map { Language(
                name = emptyWhenUnknown(it.name),
                languageLevel = emptyWhenUnknown(it.languageLevel)
            ) }
        ),
        education = this.education,
        flexibility = Flexibility(
            driverLicence = this.flexibility.driverLicence,
        ),
        keySkills = this.keySkills.map { KeySkill(
            name = it.name
        ) },
        professionalExperience = this.professionalExperience.map {
            ProfessionalExperience(
                dateFrom =  emptyWhenUnknown(it.dateFrom),
                dateTo =  emptyWhenUnknown(it.dateTo),
                country =  emptyWhenUnknown(it.country),
                city =  emptyWhenUnknown(it.city),
                company =  emptyWhenUnknown(it.company),
                profession =  emptyWhenUnknown(it.profession),
                professionDe =  emptyWhenUnknown(it.professionDe),
                professionEng =  emptyWhenUnknown(it.professionEng),
                experienceTaskDe = it.experienceTaskDe.map { Task(emptyWhenUnknown(it.name)) },
                experienceTaskEng = it.experienceTaskEng.map { Task(emptyWhenUnknown(it.name)) },
            )
        }
    )

private fun emptyWhenUnknown(value: String) =
    if (value.uppercase() == "UNDEFINED") "" else value