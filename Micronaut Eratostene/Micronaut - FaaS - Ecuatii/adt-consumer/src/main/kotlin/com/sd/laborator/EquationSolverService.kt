package com.sd.laborator

import jakarta.inject.Singleton

@Singleton
class EquationSolverService {
    fun findPairs(listA: List<Int>, listB: List<Int>): List<Pair<Int, Int>> {
        return listA.flatMap{ e1 ->
            listB.map {  e2 -> e1 to e2 }
        }.filter {
            it.first * it.second == it.first + 3 * it.second
        }
    }
}
