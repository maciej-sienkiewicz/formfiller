package pl.potentiai.formfiller.domain.candidateprofile.enhancer

import pl.potentiai.formfiller.domain.candidateprofile.model.CandidateProfile

class DriverLicenceEnhancer: CandidateProfileEnhancer {
    override fun enhance(candidateProfile: CandidateProfile): CandidateProfile =
        candidateProfile.copy(
            flexibility = candidateProfile.flexibility.copy(
                driverLicence = candidateProfile.flexibility.driverLicence.toMutableSet().apply {
                    if ("B" in this && "E" in this && "B+E" !in this) add("B+E")
                    if ("D" in this && "E" in this && "D+E" !in this) add("D+E")
                }.toList()
            )
        )
}