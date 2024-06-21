package com.sd.laborator;

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Supplier
import javax.inject.Inject
import kotlin.random.Random

@FunctionBean("eratostene")
class EratosteneFunction : FunctionInitializer(), Supplier<EratosteneResponse> {
    @Inject
    private lateinit var eratosteneSieveService: EratosteneSieveService

    private val LOG: Logger = LoggerFactory.getLogger(EratosteneFunction::class.java)

//    override fun apply(msg: EratosteneRequest): EratosteneResponse {
//        // preluare numar din parametrul de intrare al functiei



    override fun get(): EratosteneResponse {
        val A = eratosteneSieveService.mulA()
        val B = eratosteneSieveService.mulB()
        val C = eratosteneSieveService.mulC(A, B)
        val response = EratosteneResponse()

        response.setIntersection(C)
        response.setMessage("Bv, ai stil")

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
    function.run(args) { function.get() }
}