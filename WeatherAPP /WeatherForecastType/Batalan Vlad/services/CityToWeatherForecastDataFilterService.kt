package com.sd.laborator.services

import com.sd.laborator.interfaces.CityListInterface
import com.sd.laborator.interfaces.FilterInterface
import com.sd.laborator.interfaces.LocationSearchInterface
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.stereotype.Service
import java.net.URL
import org.json.JSONObject
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class CityToWeatherForecastDataFilterService(private val weatherForecastService: WeatherForecastService,
                                             private val locationSearchService: LocationSearchService,
                                             private val timeService: TimeService){


    fun getWeatherDataFromCityList(cityList: List<String>, filterList:List<FilterInterface>): List<WeatherForecastData> {
        // pentru fiecare, il tranform in pojo
        val pojoList:MutableList<WeatherForecastData> = mutableListOf()
        cityList.forEach { city ->
            // obtinem forecast data dupa orasul respectiv
            val locationId = locationSearchService.getLocationId(city)

            val result:WeatherForecastData
            // dacă locaţia nu a fost găsită, răspunsul va fi corespunzător
            if (locationId == -1) {
                result = WeatherForecastData(
                    city,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            }
            else
            {
                // pe baza ID-ului de locaţie, se interoghează al doilea serviciu care returnează datele meteo
                // încapsulate într-un obiect POJO
                result = weatherForecastService.getForecastData(locationId)

                // Actualizez data
                result.date = timeService.getCurrentTime()
            }
            pojoList.add(result)
        }

        // Apply filters
        var resultList:List<WeatherForecastData> = pojoList
        filterList.forEach{filter ->
            resultList = resultList.filter { filter.execute(it) }.toList()
        }
        return resultList
    }
}