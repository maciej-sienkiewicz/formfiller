package pl.potentiai.formfiller.configuration.infrastructure.metrics

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.potentiai.formfiller.infrastructure.metrics.MetricsAspect
import pl.potentiai.formfiller.infrastructure.metrics.TimeableWrapper

@Configuration
class MetricsConfiguration {

    @Bean
    fun timeableWrapper(meterRegistry: MeterRegistry): TimeableWrapper =
        TimeableWrapper(meterRegistry)

    @Bean
    fun metricsAspect(meterRegistry: MeterRegistry): MetricsAspect {
        return MetricsAspect(meterRegistry)
    }
}