package pl.potentiai.formfiller.infrastructure.extraction

import org.apache.poi.xwpf.usermodel.XWPFDocument
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.ContentExtractor
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.TextContent
import java.io.InputStream

class PoiDocxExtractor : ContentExtractor {

    override suspend fun extractRawString(inputStream: InputStream): TextContent {
        val docxText = extractDocxText(inputStream)
        return TextContent(docxText, docxText.length)
    }

    private fun extractDocxText(stream: InputStream): String =
        stream.use { input ->
            XWPFDocument(input).use { doc ->
                doc.paragraphs
                    .joinToString(separator = "\n") { paragraph -> paragraph.text }
            }
        }
}