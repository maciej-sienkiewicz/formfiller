package pl.potentiai.formfiller.infrastructure.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Timer

class TimeableWrapper(private val meterRegistry: MeterRegistry) {

   suspend fun <T> metricTimeExecution(metricName: String, tags: List<Tag>, method: suspend () -> T): T {
       val timer = Timer.builder(metricName)
           .tags(tags)
           .publishPercentileHistogram(true)
           .publishPercentiles(0.9)
           .register(meterRegistry)
       val sample = Timer.start(meterRegistry)
        try {
            return method()
        } finally {
            sample.stop(timer)
        }
    }
}