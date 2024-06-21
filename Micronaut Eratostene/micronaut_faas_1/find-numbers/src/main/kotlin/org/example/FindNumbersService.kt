package org.example

import io.micronaut.context.annotation.Prototype
import kotlin.random.Random


@Prototype
class FindNumbersService {
    private val multimeA = List(100){ Random.nextInt(0, 100)}.sorted()
    private val multimeB = List(100){ Random.nextInt(0, 100)}.sorted()

    fun findNumbers(): List<Pair<Int, Int>>{
        val result = mutableListOf<Pair<Int, Int>>()

        multimeA.forEach {
            a ->
                multimeB.forEach loop@{
                    b->
                        if( a * b == a + ( b * 3)) result += Pair(a, b)
                }
        }

        return result.toList()
    }

}