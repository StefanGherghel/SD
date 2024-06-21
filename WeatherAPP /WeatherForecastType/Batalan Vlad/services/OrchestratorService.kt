package com.sd.laborator.services

import com.sd.laborator.filters.NotNullHumidityFilter
import com.sd.laborator.filters.NotNullMaxTemperatureFilter
import com.sd.laborator.interfaces.CityListInterface
import com.sd.laborator.interfaces.FilterInterface
import com.sd.laborator.interfaces.LocationSearchInterface
import com.sd.laborator.interfaces.OrchestratorServiceInterface
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.stereotype.Service
import java.net.URL
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class OrchestratorService(private val weatherForecastService: WeatherForecastService,
                          private val locationSearchService: LocationSearchService,
                          private val timeService: TimeService,
                          private val cityListService: CityListService,
                          private val liniarRegressionService: LiniarRegressionService,
                          private val cityToWeatherForecastDataFilterService: CityToWeatherForecastDataFilterService) : OrchestratorServiceInterface {

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

    override fun getRegressionAngle(): Double {
        // obtin lista de orase
        val cityList = cityListService.getCityList()

        val filters = listOf<FilterInterface>(NotNullMaxTemperatureFilter(), NotNullHumidityFilter())

        // pentru fiecare, il tranform in pojo
        val pojoList:List<WeatherForecastData> = cityToWeatherForecastDataFilterService
                                                .getWeatherDataFromCityList(cityList, filters)

        // utilizand lista de pojo-uri, obtin unghiul necesar
        return liniarRegressionService.getRegressionAngle(pojoList)
    }
}