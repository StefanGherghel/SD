package com.sd.laborator.interfaces

import com.sd.laborator.model.Beer

interface BeerDAO {
    // Create
    fun createBeerTable()
    fun addBeer(beer: Beer)

    // Retrieve
    fun getBeers(): String
    fun getBeerByName(name: String): String?
    fun getBeerByPrice(price: Float): String?

    // Update
    fun updateBeer(beer: Beer)

    // Delete
    fun deleteBeer(name: String)

}