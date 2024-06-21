package com.sd.laborator.services

import com.sd.laborator.interfaces.WeatherAppVisualisationInterface
import org.springframework.stereotype.Service
import java.net.URL

// deci serviciul asta incapsuleaza interfata
@Service
class WeatherAppVisualisationService : WeatherAppVisualisationInterface{
    override fun getForecastData(locationName: String): String {
        val vremeaDataURL = URL("http://localhost:8080/getforecast/${locationName}")
        return vremeaDataURL.readText()
    }
}