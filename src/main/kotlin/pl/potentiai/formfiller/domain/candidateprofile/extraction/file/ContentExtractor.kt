package pl.potentiai.formfiller.domain.candidateprofile.extraction.file

import java.io.InputStream

interface ContentExtractor {
    suspend fun extractRawString(inputStream: InputStream): TextContent
}

data class TextContent(val content: String, val length: Int) {
    fun isAllowedSize() = length < 100000
}