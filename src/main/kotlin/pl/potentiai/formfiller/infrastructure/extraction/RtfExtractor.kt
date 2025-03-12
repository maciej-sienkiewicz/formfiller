package pl.potentiai.formfiller.infrastructure.extraction

import org.apache.poi.xwpf.usermodel.XWPFDocument
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.ContentExtractor
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.TextContent
import java.io.InputStream
import javax.swing.text.Document
import javax.swing.text.rtf.RTFEditorKit

class RtfExtractor : ContentExtractor {
    override suspend fun extractRawString(inputStream: InputStream): TextContent {
        val rtfEditorKit = RTFEditorKit()
        val document: Document = rtfEditorKit.createDefaultDocument()
        inputStream.use { rtfEditorKit.read(it, document, 0) }
        return TextContent(document.getText(0, document.length), document.length)
    }

    private fun extractDocxText(stream: InputStream): String =
        stream.use { input ->
            XWPFDocument(input).use { doc ->
                doc.paragraphs
                    .joinToString(separator = "\n") { paragraph -> paragraph.text }
            }
        }
}