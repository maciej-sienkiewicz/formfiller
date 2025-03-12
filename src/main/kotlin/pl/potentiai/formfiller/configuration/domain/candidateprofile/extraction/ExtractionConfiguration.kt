package pl.potentiai.formfiller.configuration.domain.candidateprofile.extraction

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.ContentExtractor
import pl.potentiai.formfiller.domain.candidateprofile.extraction.file.ContentExtractorStrategy
import pl.potentiai.formfiller.domain.common.ai.AIClient
import pl.potentiai.formfiller.infrastructure.extraction.*

@Configuration
class ExtractionConfiguration {

    @Bean
    fun pdfToJpgBase64(): PDFToJpgBase64 =
        PDFToJpgBase64()

    @Bean
    fun aiExtractor(pdfToJpgBase64: PDFToJpgBase64, aiClient: AIClient, ): AIExtractor =
        AIExtractor(pdfToJpgBase64, aiClient)

    @Bean
    fun pdfboxExtractor(): PDFBoxExtractor =
        PDFBoxExtractor()

    @Bean
    fun poiDocxExtractor(): PoiDocxExtractor =
        PoiDocxExtractor()

    @Bean
    fun rtfExtractor(): RtfExtractor =
        RtfExtractor()

    @Bean
    fun docExtractor(): PoiDocExtractor =
        PoiDocExtractor()

    @Bean
    fun contentExtractorStrategy(
        pdfboxExtractor: PDFBoxExtractor,
        poiDocxExtractor: PoiDocxExtractor,
        rtfExtractor: RtfExtractor,
        docExtractor: PoiDocExtractor,
        aiExtractor: AIExtractor): ContentExtractorStrategy =
        ContentExtractorStrategy(
            pdfBoxExtractor = pdfboxExtractor,
            docsxExtractor = poiDocxExtractor,
            rtfExtractor = rtfExtractor,
            docExtractor = docExtractor,
            aiExtractor = aiExtractor
        )
}