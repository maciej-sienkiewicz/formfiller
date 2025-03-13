package pl.potentiai.formfiller.infrastructure.extraction

import org.odftoolkit.odfdom.doc.OdfTextDocument
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.ContentExtractor
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.TextContent
import java.io.InputStream

class OdtExtractor : ContentExtractor {

    override suspend fun extractRawString(inputStream: InputStream): TextContent {
        val odtText = extractOdtText(inputStream)
        return TextContent(odtText, odtText.length)
    }

    private fun extractOdtText(inputStream: InputStream): String {
        return inputStream.use { stream ->
            OdfTextDocument.loadDocument(stream).use { document ->
                // Pobranie całego tekstu z dokumentu ODT
                val textExtractor = StringBuilder()

                // Iteracja przez wszystkie paragafy w dokumencie
                document.contentRoot.getElementsByTagName("text:p").let { paragraphs ->
                    for (i in 0 until paragraphs.length) {
                        val paragraph = paragraphs.item(i)
                        textExtractor.append(paragraph.textContent)
                        textExtractor.append("\n")
                    }
                }

                // Dodatkowe wyciąganie z tabel, jeśli istnieją
                document.contentRoot.getElementsByTagName("table:table-cell").let { cells ->
                    for (i in 0 until cells.length) {
                        val cell = cells.item(i)
                        textExtractor.append(cell.textContent)
                        textExtractor.append(" ")
                    }
                }

                textExtractor.toString().trim()
            }
        }
    }
}