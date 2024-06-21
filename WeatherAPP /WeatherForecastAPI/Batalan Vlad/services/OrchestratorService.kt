package com.sd.laborator.services

import com.sd.laborator.interfaces.OrchestratorServiceInterface
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.stereotype.Service

@Service
class OrchestratorService(private val weatherForecastService: WeatherForecastService,
                          private val locationSearchService: LocationSearchService,
                          private val timeService: TimeService) : OrchestratorServiceInterface {

    override fun getForecastData(locationName: String): WeatherForecastData {
        val locationId = locationSearchService.getLocationId(locationName)

        // dacă locaţia nu a fost găsită, răspunsul va fi corespunzător
        if (locationId == -1) {
            return WeatherForecastData(
                locationName,
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

        // pe baza ID-ului de locaţie, se interoghează al doilea serviciu care returnează datele meteo
        // încapsulate într-un obiect POJO
        val rawForecastData: WeatherForecastData = weatherForecastService.getForecastData(locationId)

        // Actualizez data
        rawForecastData.date = timeService.getCurrentTime()

        return rawForecastData
    }
}