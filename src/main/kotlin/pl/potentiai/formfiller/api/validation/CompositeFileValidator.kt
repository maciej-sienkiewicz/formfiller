package pl.potentiai.formfiller.api.validation

import org.springframework.web.multipart.MultipartFile

data class FileValidationResult private constructor(val passed: Boolean, val errorMessage: String? = null) {
    companion object {
        val SUCCESS = FileValidationResult(true)
        fun FAILED(message: String) = FileValidationResult(false, message)
    }
}

interface FileValidator {
    fun validate(multipartFile: MultipartFile): FileValidationResult
}

class EmptyFileValidator : FileValidator {
    override fun validate(multipartFile: MultipartFile): FileValidationResult =
        when (multipartFile.isEmpty) {
            true -> FileValidationResult.FAILED("Przesłany plik jest pusty")
            false -> FileValidationResult.SUCCESS
        }
}

class SupportedContentType : FileValidator {
    override fun validate(multipartFile: MultipartFile): FileValidationResult =
        when (isSupportedContentType(multipartFile.contentType)) {
            true -> FileValidationResult.FAILED("Nieobsługiwany typ pliku.")
            false -> FileValidationResult.SUCCESS
        }

    private fun isSupportedContentType(contentType: String?): Boolean {
        val supportedTypes = listOf("application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        return contentType != null && supportedTypes.contains(contentType)
    }
}

class CompositeFileValidator(private val validators: List<FileValidator>): FileValidator {
    override fun validate(multipartFile: MultipartFile): FileValidationResult {
        val failedReasons = validators.map { it.validate(multipartFile) }
            .filter { !it.passed }
            .map { it.errorMessage }
        return when (failedReasons.isNotEmpty()) {
            true -> FileValidationResult.FAILED(failedReasons.joinToString("\n"))
            false -> FileValidationResult.SUCCESS
        }
    }
}
