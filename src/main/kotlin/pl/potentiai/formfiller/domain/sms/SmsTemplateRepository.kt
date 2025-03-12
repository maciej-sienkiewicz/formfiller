package pl.potentiai.formfiller.domain.sms

interface SmsTemplateRepository {
    fun getTemplate(templateId: Long): SMSTemplate?
    fun getAll(): List<SMSTemplate>
}

data class SMSTemplate(val id : Long, val name: String, val content: String)