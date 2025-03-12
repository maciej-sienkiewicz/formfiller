package pl.potentiai.formfiller.infrastructure.metrics

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CountUsage(val metricName: String)