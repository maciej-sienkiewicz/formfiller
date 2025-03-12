package pl.potentiai.formfiller.domain.candidateprofile.enhancer

import pl.potentiai.formfiller.domain.candidateprofile.model.CandidateProfile
import pl.potentiai.formfiller.domain.candidateprofile.model.KnowledgeOfLanguages
import pl.potentiai.formfiller.domain.candidateprofile.model.Language

class LanguagesEnhancer: CandidateProfileEnhancer {

    override fun enhance(candidateProfile: CandidateProfile): CandidateProfile =
        candidateProfile
            .copy(languages = KnowledgeOfLanguages(
                candidateProfile.languages.languages
                    .filterNot { it.name.lowercase().contains("polski") }
                    .map { Language(mapKey(it.name), it.languageLevel) }
            ))

    private fun mapKey(value: String) =
        when(value) {
            "Holenderski" -> "Niderlandzki"
            else -> value
        }.lowercase().replaceFirstChar { it.uppercaseChar() }
}