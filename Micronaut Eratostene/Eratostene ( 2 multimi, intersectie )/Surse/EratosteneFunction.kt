package com.sd.laborator;

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function
import javax.inject.Inject

@FunctionBean("eratostene")
class EratosteneFunction : FunctionInitializer(), Function<EratosteneRequest, EratosteneResponse> {
    @Inject
    private lateinit var eratosteneSieveService: EratosteneSieveService

    private val LOG: Logger = LoggerFactory.getLogger(EratosteneFunction::class.java)

    override fun apply(msg: EratosteneRequest): EratosteneResponse {
        val number = msg.getNumber()
        val response = EratosteneResponse()

        val a = eratosteneSieveService.randomList(number)
        val b = eratosteneSieveService.randomList(number)
        val c = eratosteneSieveService.listC(a, b)

        response.setIntersection(c)

        LOG.info("Calcul incheiat!")
        return response
    }
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar 
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) { 
    val function = EratosteneFunction()
    function.run(args, { context -> function.apply(context.get(EratosteneRequest::class.java))})
}