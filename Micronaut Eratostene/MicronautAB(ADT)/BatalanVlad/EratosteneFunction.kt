package com.sd.laborator;

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Supplier
import javax.inject.Inject

// ai inteles de ce supplier?
@FunctionBean("eratostene")
class EratosteneFunction : FunctionInitializer(), Supplier<SimpleResponse> {
    @Inject
    private lateinit var createADTService: CreateADTService

    private val LOG: Logger = LoggerFactory.getLogger(EratosteneFunction::class.java)

    override fun get(): SimpleResponse {
        // the solution
        var solution = mutableListOf<Pair<Int, Int>>()

        // Initializam A si B
        val adts = createADTService.createADTs()
        val A = adts.first
        val B = adts.second

        // Cautam perechiile necesare si le adaugam in lista
            // Pentru fiecare element din B, incerc sa gasesc corespunzator in A

        B.forEach { b ->
            if(b != 1 && ((3*b) % (b-1)) == 0){
                // pt b = 1, formula nu e buna
                // b are forma 3k + 1
                // rezultatul oricum este int
                val analitycA = 3*b / (b-1)

                // caut in A sa vad daca gasesc
                val found = A.find { it == analitycA }
                if(found != null){
                    // am gasit, adaug la solutie
                    solution.add(Pair(analitycA, b))
                }
            }
        }
        
        // adaug la solutie
        val simpleResponse = SimpleResponse()
        simpleResponse.setPairs(solution)

        // Trimitem raspunsul mai departe
        return simpleResponse
    }
}

fun main(args : Array<String>) { 
    val function = EratosteneFunction()
    function.run(args) { function.get() }
}