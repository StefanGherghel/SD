package com.sd.laborator.Filter

import com.sd.laborator.pojo.FilteredWeatherForecastData
import com.sd.laborator.pojo.WeatherForecastData

interface IFilter {
    fun filter(weatherForecastData: WeatherForecastData) : FilteredWeatherForecastData
}