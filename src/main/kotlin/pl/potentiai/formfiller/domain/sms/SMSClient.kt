package pl.potentiai.formfiller.domain.sms

interface SmsClient {
    fun sendSms(recipients: List<String>, message: String)
}