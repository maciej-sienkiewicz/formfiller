package pl.potentiai.formfiller.infrastructure.metrics

import io.micrometer.core.instrument.MeterRegistry
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import java.security.Principal

@Aspect
class MetricsAspect(private val meterRegistry: MeterRegistry) {

    @Around("@annotation(countUsage)")
    fun recordUsage(joinPoint: ProceedingJoinPoint, countUsage: CountUsage): Any? {
        val principalName = joinPoint.args.filterIsInstance<Principal>().firstOrNull()?.name ?: "anonymous"

        meterRegistry.counter(countUsage.metricName, "user", principalName).increment()

        return joinPoint.proceed()
    }
}