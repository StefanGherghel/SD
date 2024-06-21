package com.sd.laborator.interfaces

import com.sd.laborator.pojo.WeatherForecastData

interface OrchestratorServiceInterface {
    fun getForecastData(locationName: String): WeatherForecastData
}