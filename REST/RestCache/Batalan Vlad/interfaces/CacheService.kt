package com.sd.laborator.interfaces

import com.sd.laborator.pojo.Person

interface CacheService {
    // checks if the element is in the cache list
    fun searchExists(lastNameFilter: String, firstNameFilter: String, telephoneNumberFilter: String) : Boolean

    // get the element from the cache list
    fun getSearchCache(lastNameFilter: String, firstNameFilter: String, telephoneNumberFilter: String) : List<Person>

    // store result in cache
    fun storeResult(lastNameFilter: String, firstNameFilter: String, telephoneNumberFilter: String, result:List<Person>)
}