package pl.potentiai.formfiller.infrastructure.extraction

import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.ContentExtractor
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.TextContent
import java.io.InputStream
import java.nio.charset.StandardCharsets

class TxtExtractor : ContentExtractor {

    override suspend fun extractRawString(inputStream: InputStream): TextContent {
        val txtText = extractTxtText(inputStream)
        return TextContent(txtText, txtText.length)
    }

    private fun extractTxtText(inputStream: InputStream): String {
        return inputStream.use { stream ->
            // Próbujemy odczytać używając UTF-8, który obsługuje większość znaków diakrytycznych
            val text = stream.readAllBytes().toString(StandardCharsets.UTF_8)

            // Jeśli tekst zawiera znaki zapytania (co może wskazywać na problemy z kodowaniem),
            // możemy spróbować inne kodowanie, np. ISO-8859-2 dla Europy Środkowej
            if (text.contains("�")) {
                try {
                    return String(stream.readAllBytes(), charset("ISO-8859-2"))
                } catch (e: Exception) {
                    // Jeśli to się nie powiedzie, wracamy do UTF-8
                    return text
                }
            }

            text
        }
    }
}