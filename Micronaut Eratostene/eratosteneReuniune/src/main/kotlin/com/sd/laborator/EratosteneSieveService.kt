package com.sd.laborator


import java.util.*
import javax.inject.Singleton
import kotlin.random.Random.Default.nextInt

@Singleton
class EratosteneSieveService {
    //multimea C ( reuniunea dintre A si B)
    fun listC(A: List<Int>, B:List<Int>) : Set<Int>{
        return A.union(B)
    }

    //multime de 100 elemente generata random
    fun randomList(maxNumber: Int) : List<Int>{
        val rezultat = mutableListOf<Int>()

        for(i in 1 until 101){
            rezultat.add(nextInt(0,maxNumber))
        }
        return rezultat
    }
}