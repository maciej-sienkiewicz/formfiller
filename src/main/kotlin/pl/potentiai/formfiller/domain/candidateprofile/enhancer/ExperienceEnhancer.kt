package pl.potentiai.formfiller.domain.candidateprofile.enhancer

import pl.potentiai.formfiller.domain.candidateprofile.model.CandidateProfile

class ExperienceEnhancer: CandidateProfileEnhancer {

    override fun enhance(candidateProfile: CandidateProfile): CandidateProfile =
        candidateProfile
            .copy(professionalExperience = candidateProfile.professionalExperience.sortedByDescending { it.dateFrom }
            )
}