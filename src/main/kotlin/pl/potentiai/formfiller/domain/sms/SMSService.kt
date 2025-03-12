package pl.potentiai.formfiller.domain.sms

import org.springframework.stereotype.Service

@Service
class SmsService(
    private val smsTemplateRepository: SmsTemplateRepository,
    private val smsClient: SmsClient
) {

    fun sendSms(name: String, templateId: Long, sender: String, recipients: List<String>) {
        val template = smsTemplateRepository.getTemplate(templateId)
            ?: throw IllegalArgumentException("Szablon SMS o ID $templateId nie został znaleziony")
        val content = template.content
            .replace("{{PHONE_NUMBER}}", sender)
            .replace("{{NAME}}", name)
        smsClient.sendSms(recipients, content)
    }

    fun getTemplates() =
        smsTemplateRepository.getAll()
}
