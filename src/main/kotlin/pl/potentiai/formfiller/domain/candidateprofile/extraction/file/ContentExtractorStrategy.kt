package pl.potentiai.formfiller.domain.candidateprofile.extraction.file

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.InputStream

class ContentExtractorStrategy(
    private val pdfBoxExtractor: ContentExtractor,
    private val docsxExtractor: ContentExtractor,
    private val rtfExtractor: ContentExtractor,
    private val docExtractor: ContentExtractor,
    private val aiExtractor: ContentExtractor,
) {
    fun byFileExtension(fileName: String): ContentExtractor =
        when {
            fileName.endsWith("pdf") -> WithFallback(aiExtractor, pdfBoxExtractor)
            fileName.endsWith("docx") -> docsxExtractor
            fileName.endsWith("doc") -> docExtractor
            fileName.endsWith("rtf") -> rtfExtractor
            else -> throw IllegalArgumentException("Nieobsługiwany format pliku: $fileName")
        }
}

data class WithFallback(private val mainExtractor: ContentExtractor, private val fallbackExtractor: ContentExtractor): ContentExtractor {
    override suspend fun extractRawString(inputStream: InputStream): TextContent {
        val readAllBytes = withContext(Dispatchers.IO) {
            inputStream.readAllBytes()
        }

        val extracted = mainExtractor.extractRawString(ByteArrayInputStream(readAllBytes))
        println(extracted)
        return if(extracted.length > 500) {
            extracted
        } else {
            fallbackExtractor.extractRawString(ByteArrayInputStream(readAllBytes))
        }
    }

}