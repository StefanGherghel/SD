package com.sd.laborator.interfaces

import com.sd.laborator.pojo.WeatherForecastData

interface WeatherAppVisualisationInterface {
    fun getForecastData(locationName: String): String
}