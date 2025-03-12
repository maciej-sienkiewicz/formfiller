package pl.potentiai.formfiller

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class PotentiAiFormFillerApplication

fun main(args: Array<String>) {
	runApplication<PotentiAiFormFillerApplication>(*args)
}
