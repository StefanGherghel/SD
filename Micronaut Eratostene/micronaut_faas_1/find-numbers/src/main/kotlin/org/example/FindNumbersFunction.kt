package org.example

import java.util.function.Supplier
import io.micronaut.function.executor.FunctionInitializer
import io.micronaut.function.FunctionBean
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

@FunctionBean("find-numbers")
class FindNumbersFunction : FunctionInitializer(), Supplier<FindNumbersResponse> {

    @Inject
    private lateinit var findNumbersService: FindNumbersService

    private val logger: Logger = LoggerFactory.getLogger(FindNumbersFunction::class.java)



    override fun get(): FindNumbersResponse {
        val result = findNumbersService.findNumbers()
        val response = FindNumbersResponse()
        val message = if (result.isEmpty()) {
            "Nu au fost gasite valori care sa indeplineasca conditia!"
        }else{
            "Operatie efectuata cu success!"
        }
        logger.info(message)
        response.setMessage(message)
        response.setPrimes(result)
        return response
    }
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) {
    val function = FindNumbersFunction()
    function.run(args) { function.get() }
}