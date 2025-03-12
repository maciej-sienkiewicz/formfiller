package pl.potentiai.formfiller.infrastructure.sms

import org.slf4j.LoggerFactory
import pl.potentiai.formfiller.domain.sms.SmsClient
import pl.smsapi.api.SmsFactory
import pl.smsapi.api.action.sms.SMSSend
import pl.smsapi.exception.SmsapiException

class SMSApiClient(
    private val smsFactory: SmsFactory
): SmsClient {
    private val logger = LoggerFactory.getLogger(SMSApiClient::class.java)

    override fun sendSms(recipients: List<String>, message: String) {
        try {
            val action: SMSSend = smsFactory.actionSend(recipients.toTypedArray(), message)
            action.execute()
        } catch (ex: SmsapiException) {
            logger.error("SMS sending failed: ${ex.message}", ex)
            throw SmsSendingException("SMS sending failed", ex)
        } catch (ex: IllegalArgumentException) {
            logger.error("Invalid argument provided: ${ex.message}", ex)
            throw SmsSendingException("SMS sending failed due to invalid input", ex)
        } catch (ex: Exception) {
            logger.error("Unexpected error occurred: ${ex.message}", ex)
            throw SmsSendingException("SMS sending failed due to an unexpected error", ex)
        }
    }
}

class SmsSendingException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)