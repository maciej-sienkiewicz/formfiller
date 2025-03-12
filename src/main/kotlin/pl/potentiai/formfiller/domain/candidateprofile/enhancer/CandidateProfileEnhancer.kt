package pl.potentiai.formfiller.domain.candidateprofile.enhancer

import pl.potentiai.formfiller.domain.candidateprofile.model.CandidateProfile

interface CandidateProfileEnhancer {
    fun enhance(candidateProfile: CandidateProfile): CandidateProfile
}

class CompositeCandidateProfileEnhancer(private val delegates: List<CandidateProfileEnhancer>): CandidateProfileEnhancer {
    override fun enhance(candidateProfile: CandidateProfile): CandidateProfile =
        delegates.fold(candidateProfile) { profile, enhancer -> enhancer.enhance(profile) }
}