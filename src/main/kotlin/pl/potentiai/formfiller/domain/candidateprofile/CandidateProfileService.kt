package pl.potentiai.formfiller.domain.candidateprofile

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.web.multipart.MultipartFile
import pl.potentiai.formfiller.domain.FileProcessingError
import pl.potentiai.formfiller.domain.candidateprofile.enhancer.CompositeCandidateProfileEnhancer
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.ContentExtractorStrategy
import pl.potentiai.formfiller.domain.candidateprofile.model.CandidateProfile
import pl.potentiai.formfiller.domain.common.ai.AIClient
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class CandidateProfileService(
    private val contentExtractorStrategy: ContentExtractorStrategy,
    private val domainObjectEnhancers: CompositeCandidateProfileEnhancer,
    private val aiClient: AIClient,
    private val meterRegistry: MeterRegistry,
    ) {

    private val stats = ConcurrentHashMap<Pair<String, String>, AtomicInteger>()

    suspend fun process(file: MultipartFile): CandidateProfile {
        val fileExtension = file.getFileExtension()
        meterRegistry.counter("cv.processed", "extension", fileExtension).increment()

        val cvContent = contentExtractorStrategy.byFileExtension(fileExtension)
            .extractRawString(file.inputStream)
        meterRegistry.summary("cv.size", "extension", fileExtension)
            .record(cvContent.content.length.toDouble())

        val candidateProfile = when (cvContent.isAllowedSize()) {
            true -> aiClient.fromRawTextToCandidateProfile(cvContent.content)
            false -> throw FileProcessingError("CV, które przetwarzasz ma zbyt wiele znaków.")
        }.let { domainObjectEnhancers.enhance(it) }

        bullshitStats(candidateProfile, fileExtension)

        return candidateProfile
    }

    fun getStats(): Map<String, Any> {
        return mapOf(
            "fileExtensionNameStats" to stats.mapKeys { "${it.key.first} -> ${it.key.second}" }
                .mapValues { it.value.get() }
        )
    }

    private fun bullshitStats(candidateProfile: CandidateProfile, fileExtension: String) {
        val fullName = "${candidateProfile.personalInfo.firstName} ${candidateProfile.personalInfo.lastName}"

        val key = fileExtension to fullName
        stats.computeIfAbsent(key) { AtomicInteger(0) }.incrementAndGet()
    }

    private fun MultipartFile.getFileExtension() =
        this.originalFilename?.lowercase()
            ?.substringAfterLast(".", "unknown") ?: "unknown"
}