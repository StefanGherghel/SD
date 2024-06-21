package org.example.interfaces

import org.example.model.Beer

interface CreateBeerInterface {
    //Create
    fun createBeerTable()
    fun addBeer(beer: Beer)
}