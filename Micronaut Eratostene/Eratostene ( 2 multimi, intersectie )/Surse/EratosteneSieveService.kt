package com.sd.laborator

import java.util.*
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class EratosteneSieveService {

    //multimea C ( intersectia dintre A si B)
    fun listC(A: List<Int>, B:List<Int>) : Set<Int>{
        return A.intersect(B)
    }

    //multime de 100 elemente generata random
    fun randomList(maxNumber: Int) : List<Int>{
        var rezultat = mutableListOf<Int>()

        for(i in 1 until 101){
            rezultat.add(Random.nextInt(0,maxNumber))
        }
        return rezultat
    }
}