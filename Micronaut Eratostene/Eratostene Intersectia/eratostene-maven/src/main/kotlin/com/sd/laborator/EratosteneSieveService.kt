package com.sd.laborator

import java.util.*
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class EratosteneSieveService {
    fun mulA(): List<Int> {
        var result  = mutableListOf<Int>()
        for(i in 0..100){
            result.add(Random.nextInt(0,100))
        }
        return result
    }

    fun mulB(): List<Int> {
        var result  = mutableListOf<Int>()
        for(i in 0..100){
            result.add(Random.nextInt(0,100))
        }
        return result
    }

    fun mulC(A: List<Int>, B: List<Int>): Set<Int> {
        return A.intersect(B)
    }
}