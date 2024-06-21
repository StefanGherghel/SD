package com.sd.laborator.services

import com.sd.laborator.interfaces.CityListInterface
import com.sd.laborator.interfaces.LocationSearchInterface
import org.springframework.stereotype.Service
import java.net.URL
import org.json.JSONObject
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class CityListService : CityListInterface {
    private val file = File("src/main/kotlin/com/sd/laborator/resources/cities.txt")

    override fun getCityList(): List<String> {
        val cityList = mutableListOf<String>()
        file.forEachLine { cityList.add(it) }
        return cityList

    }
}