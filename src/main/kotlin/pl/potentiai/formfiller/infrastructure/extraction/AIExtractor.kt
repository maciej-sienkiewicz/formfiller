package pl.potentiai.formfiller.infrastructure.extraction

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.ContentExtractor
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.TextContent
import pl.potentiai.formfiller.domain.common.ai.AIClient
import java.io.InputStream

class AIExtractor(
    private val pdfToJpgBase64: PDFToJpgBase64,
    private val aiClient: AIClient,
    ): ContentExtractor {

    override suspend fun extractRawString(inputStream: InputStream): TextContent = coroutineScope {
        pdfToJpgBase64.convertPdfToBase64Jpg(inputStream)
            .map { base64 ->
                async { aiClient.extractContent(base64) }
            }
            .awaitAll()
            .joinToString(separator = " ")
            .let { TextContent(it, it.length) }
    }
}