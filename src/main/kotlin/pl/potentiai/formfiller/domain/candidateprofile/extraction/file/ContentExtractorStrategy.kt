package pl.potentiai.formfiller.domain.candidateprofile.extraction.file

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.potentiai.formfiller.infrastructure.extraction.OdtExtractor
import pl.potentiai.formfiller.infrastructure.extraction.TxtExtractor
import java.io.ByteArrayInputStream
import java.io.InputStream

class ContentExtractorStrategy(
    private val pdfBoxExtractor: ContentExtractor,
    private val docsxExtractor: ContentExtractor,
    private val rtfExtractor: ContentExtractor,
    private val docExtractor: ContentExtractor,
    private val aiExtractor: ContentExtractor,
    private val odtExtractor: ContentExtractor,
    private val txtExtractor: ContentExtractor
) {
    fun byFileExtension(fileName: String): ContentExtractor =
        when {
            fileName.endsWith("pdf") -> WithFallback(aiExtractor, pdfBoxExtractor)
            fileName.endsWith("docx") -> docsxExtractor
            fileName.endsWith("doc") -> docExtractor
            fileName.endsWith("rtf") -> rtfExtractor
            fileName.endsWith("odt") -> odtExtractor
            fileName.endsWith("txt") -> txtExtractor
            else -> throw IllegalArgumentException("Nieobsługiwany format pliku: $fileName")
        }
}

data class WithFallback(private val mainExtractor: ContentExtractor, private val fallbackExtractor: ContentExtractor): ContentExtractor {
    override suspend fun extractRawString(inputStream: InputStream): TextContent {
        val readAllBytes = withContext(Dispatchers.IO) {
            inputStream.readAllBytes()
        }

        val extracted = mainExtractor.extractRawString(ByteArrayInputStream(readAllBytes))
        return if(extracted.length > 500) {
            extracted
        } else {
            fallbackExtractor.extractRawString(ByteArrayInputStream(readAllBytes))
        }
    }

}