package com.sd.laborator.interfaces

import com.sd.laborator.model.Book
import com.sd.laborator.model.Cache

interface CacheDAO {
    fun getCache(): Set<Cache?>
    fun addCache(cache:Cache)
    fun findByQuery(query: String): Set<Cache?>
}