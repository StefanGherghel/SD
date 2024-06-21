package org.example

import io.micronaut.core.annotation.Introspected

@Introspected
class FindNumbersResponse {
    private var message: String? = null
    private var foundNumbers: List<Pair<Int, Int>>? = null

    fun getFoundNumbers(): List<Pair<Int, Int>>? = foundNumbers

    fun setPrimes(numbersList: List<Pair<Int, Int>>?){
        if (numbersList!!.isEmpty()){
            this.foundNumbers = listOf<Pair<Int,Int>>()
        }
        else {
            this.foundNumbers = numbersList
        }
    }

    fun getMessage(): String? = message

    fun setMessage(message: String?){
        this.message = message
    }
}