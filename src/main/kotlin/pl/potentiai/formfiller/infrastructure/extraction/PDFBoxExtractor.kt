package pl.potentiai.formfiller.infrastructure.extraction

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.poi.xwpf.usermodel.XWPFDocument
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.ContentExtractor
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.TextContent
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class PDFBoxExtractor: ContentExtractor {

    override suspend fun extractRawString(inputStream: InputStream): TextContent = inputStream.use { stream ->
        val docxContent = convertPDFToDOCX(stream)

        ByteArrayInputStream(docxContent).use { docxStream ->
            XWPFDocument(docxStream).use { docxDocument ->
                val extractedText = docxDocument.paragraphs.joinToString("\n") { it.text.trim() }
                TextContent(extractedText, extractedText.length)
            }
        }
    }

    private fun convertPDFToDOCX(inputStream: InputStream): ByteArray {
        val pdfText = extractTextFromPDF(inputStream)

        val outputStream = ByteArrayOutputStream()
        XWPFDocument().use { document ->
            pdfText.lines().forEach { line ->
                val paragraph = document.createParagraph()
                paragraph.createRun().apply {
                    setText(line.trim())
                    fontSize = 12
                }
            }
            document.write(outputStream)
        }
        return outputStream.toByteArray()
    }

    private fun extractTextFromPDF(inputStream: InputStream): String {
        PDDocument.load(inputStream).use { document ->
            val stripper = PDFTextStripper()
            return stripper.getText(document).trim()
        }
    }
}