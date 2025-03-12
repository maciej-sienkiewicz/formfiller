package pl.potentiai.formfiller.infrastructure.extraction

import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.ContentExtractor
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.TextContent
import java.io.InputStream

class PoiDocExtractor : ContentExtractor {

    override suspend fun extractRawString(inputStream: InputStream): TextContent {
        val docText = extractDocText(inputStream)
        return TextContent(docText, docText.length)
    }

    private fun extractDocText(stream: InputStream): String =
        stream.use { input ->
            HWPFDocument(input).use { doc ->
                WordExtractor(doc).use { extractor ->
                    extractor.text
                }
            }
        }
}
