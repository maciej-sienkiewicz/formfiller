package pl.potentiai.formfiller.configuration.sms

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.potentiai.formfiller.domain.sms.SmsClient
import pl.potentiai.formfiller.domain.sms.SmsService
import pl.potentiai.formfiller.domain.sms.SmsTemplateRepository

@Configuration
class SMSServiceConfiguration {

    @Bean
    fun smsService(
        smsTemplateRepository: SmsTemplateRepository,
        smsClient: SmsClient
    ): SmsService =
        SmsService(
            smsTemplateRepository = smsTemplateRepository,
            smsClient = smsClient
        )
}