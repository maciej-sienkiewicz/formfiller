package pl.potentiai.formfiller.configuration.infrastructure.sms

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.potentiai.formfiller.domain.sms.SmsClient
import pl.potentiai.formfiller.domain.sms.SmsTemplateRepository
import pl.potentiai.formfiller.infrastructure.sms.JpaSmsTemplateRepository
import pl.potentiai.formfiller.infrastructure.sms.PostgresSMSRepository
import pl.potentiai.formfiller.infrastructure.sms.SMSApiClient
import pl.smsapi.OAuthClient
import pl.smsapi.api.SmsFactory
import pl.smsapi.proxy.ProxyNative

@Configuration
class SMSConfiguration {

    @Bean
    fun smsTemplateRepository(jpaSmsTemplateRepository: JpaSmsTemplateRepository): SmsTemplateRepository =
        PostgresSMSRepository(jpaSmsTemplateRepository)

    @Bean
    fun smsFactory(): SmsFactory {
        val client = OAuthClient("ySfcIfYGj4PK7OnsZWSf4q0b2piDbqWWKfz9bCoe")
        val proxy = ProxyNative("https://api.smsapi.pl/")
        return SmsFactory(client, proxy)
    }

    @Bean
    fun smsClient(smsFactory: SmsFactory): SmsClient =
        SMSApiClient(
            smsFactory = smsFactory
        )
}