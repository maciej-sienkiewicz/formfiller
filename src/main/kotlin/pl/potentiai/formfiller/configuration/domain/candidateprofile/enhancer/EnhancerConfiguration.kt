package pl.potentiai.formfiller.configuration.domain.candidateprofile.enhancer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.potentiai.formfiller.domain.candidateprofile.enhancer.*

@Configuration
class EnhancerConfiguration {
    @Bean
    fun experienceEnhancer(): CandidateProfileEnhancer =
        ExperienceEnhancer()

    @Bean
    fun languagesEnhancer(): CandidateProfileEnhancer =
        LanguagesEnhancer()

    @Bean
    fun driverLicenceEnhancer(): DriverLicenceEnhancer =
        DriverLicenceEnhancer()

    @Bean
    fun compositeEnhancer(
        enhancers: List<CandidateProfileEnhancer>
    ): CompositeCandidateProfileEnhancer =
        CompositeCandidateProfileEnhancer(enhancers)
}