package pl.potentiai.formfiller.configuration.domain.candidateprofile

import com.aallam.openai.client.OpenAI
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.potentiai.formfiller.domain.candidateprofile.CandidateProfileService
import pl.potentiai.formfiller.domain.candidateprofile.enhancer.CompositeCandidateProfileEnhancer
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.ContentExtractorStrategy
import pl.potentiai.formfiller.domain.common.ai.AIClient

@Configuration
class CandidateProfileServiceConfiguration {
    @Bean
    fun candidateProfileService(
        contentExtractorStrategy: ContentExtractorStrategy,
        compositeCandidateProfileEnhancer: CompositeCandidateProfileEnhancer,
        aiClient: AIClient,
        meterRegistry: MeterRegistry,
    ): CandidateProfileService =
        CandidateProfileService(
            contentExtractorStrategy = contentExtractorStrategy,
            domainObjectEnhancers = compositeCandidateProfileEnhancer,
            aiClient = aiClient,
            meterRegistry = meterRegistry
        )
}