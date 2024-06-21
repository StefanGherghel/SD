package org.example.interfaces

import org.example.pojo.Person

interface CacheServiceInterface {
    fun checkResource(uriString: String): Boolean
    fun getResource(uriString: String): List<Person?>
    fun addResource(data: Pair<String, List<Person?>>)
}