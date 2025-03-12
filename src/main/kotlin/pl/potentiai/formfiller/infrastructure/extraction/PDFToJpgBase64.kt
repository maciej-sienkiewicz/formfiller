package pl.potentiai.formfiller.infrastructure.extraction

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO
import java.util.Base64

class PDFToJpgBase64 {

    fun convertPdfToBase64Jpg(inputStream: InputStream): List<String> =
        PDDocument.load(inputStream).use { document ->
            val pdfRenderer = PDFRenderer(document)
            (0 until document.numberOfPages).map { pageIndex ->
                val bufferedImage = pdfRenderer.renderImageWithDPI(pageIndex, 300F)
                ByteArrayOutputStream().use { baos ->
                    ImageIO.write(bufferedImage, "jpg", baos)
                    val jpgBytes = baos.toByteArray()
                    val base64String = Base64.getEncoder().encodeToString(jpgBytes)
                    "data:image/jpeg;base64,$base64String"
                }
            }
        }
}