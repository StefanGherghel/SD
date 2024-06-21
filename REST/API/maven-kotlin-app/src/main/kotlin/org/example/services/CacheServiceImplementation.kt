package org.example.services

import org.example.interfaces.CacheServiceInterface
import org.example.pojo.Person
import org.springframework.stereotype.Service
import java.util.*

@Service
class CacheServiceImplementation: CacheServiceInterface {

    private val cacheMap = mutableMapOf<String, Pair<Date, List<Person?>>>()
    private val cacheLimitTime = 1000 * 60 * 30

    override fun checkResource(uriString: String): Boolean {
        val now = Date()
        return uriString in cacheMap.keys && now.time - (cacheMap[uriString]?.first?.time ?: now.time) <= cacheLimitTime
    }

    override fun getResource(uriString: String): List<Person?> {
        return cacheMap[uriString]!!.second
    }

    override fun addResource(data: Pair<String, List<Person?>>) {
        cacheMap[data.first] = Pair(Date(), data.second)
    }

}