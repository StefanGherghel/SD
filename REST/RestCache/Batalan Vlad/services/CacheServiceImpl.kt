package com.sd.laborator.services;

import com.sd.laborator.interfaces.AgendaService
import com.sd.laborator.interfaces.CacheService
import com.sd.laborator.pojo.CacheData
import com.sd.laborator.pojo.Person
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class CacheServiceImpl(private val localDateTimeUnitConvertorService: LocalDateTimeUnitConvertorService) : CacheService {

    private val cacheList: MutableList<CacheData> = mutableListOf()

    override fun searchExists(lastNameFilter: String, firstNameFilter: String, telephoneNumberFilter: String): Boolean {
        val searchString = "${lastNameFilter}/${firstNameFilter}/${telephoneNumberFilter}"
        val nowTimestamp =  LocalDateTime.now()

        cacheList.forEach {
            if(searchString == it.searchString) {
                val difference = localDateTimeUnitConvertorService.convertToUnitsDifference(it.timestamp, nowTimestamp)
                // mini mangleala, presupunem ca e folosit doar pentru aceasi zi ;)
                if (difference.years == 0 && difference.months == 0 && difference.days == 0 && difference.hours == 0 && difference.minutes < 30) {
                    return true
                }
            }
        }
        return false
    }

    override fun getSearchCache(
        lastNameFilter: String,
        firstNameFilter: String,
        telephoneNumberFilter: String
    ): List<Person> {
        val searchString = "${lastNameFilter}/${firstNameFilter}/${telephoneNumberFilter}"
        val nowTimestamp =  LocalDateTime.now()

        cacheList.forEach {
            if (searchString == it.searchString) {
                val difference = localDateTimeUnitConvertorService.convertToUnitsDifference(it.timestamp, nowTimestamp)

                // mini mangleala, presupunem ca e folosit doar pentru aceasi zi ;)
                if (difference.years == 0 && difference.months == 0 && difference.days == 0 && difference.hours == 0 && difference.minutes < 30) {
                    return it.resultList
                }
            }
        }
        return listOf()
    }

    override fun storeResult(
        lastNameFilter: String,
        firstNameFilter: String,
        telephoneNumberFilter: String,
        result: List<Person>
    ) {

        val searchString = "${lastNameFilter}/${firstNameFilter}/${telephoneNumberFilter}"
        if(!searchExists(lastNameFilter, firstNameFilter, telephoneNumberFilter)){
            cacheList.add(CacheData(
                LocalDateTime.now(),
                searchString,
                result
            ))
        }
    }
}