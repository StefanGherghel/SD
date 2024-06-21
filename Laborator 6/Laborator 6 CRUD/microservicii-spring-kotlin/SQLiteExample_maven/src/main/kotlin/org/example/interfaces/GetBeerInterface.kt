package org.example.interfaces

interface GetBeerInterface {
    //Retrieve
    fun getBeers(): String
    fun getBeerByName(name: String): String?
    fun getBeerByPrice(price: Float): String?
}