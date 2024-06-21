package com.sd.laborator

import java.util.*
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class CreateADTService {
    // implementare preluata de la https://www.geeksforgeeks.org/sieve-eratosthenes-0n-time-complexity/
    val MAX_SIZE = 100

    init {

    }

    fun createADTs(): Pair<List<Int>, List<Int>>{
        val A = mutableListOf<Int>()
        val B = mutableListOf<Int>()

        for(i in 0..MAX_SIZE){
            A.add(Random.nextInt(100))
            B.add(Random.nextInt(100))
        }

        return Pair(A, B)
    }
}