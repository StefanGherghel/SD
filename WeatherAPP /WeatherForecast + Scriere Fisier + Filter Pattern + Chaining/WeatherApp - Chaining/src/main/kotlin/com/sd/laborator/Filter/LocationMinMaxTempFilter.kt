package com.sd.laborator.Filter

import com.sd.laborator.pojo.FilteredWeatherForecastData
import com.sd.laborator.pojo.WeatherForecastData

class LocationMinMaxTempFilter : IFilter {
    override fun filter(weatherForecastData: WeatherForecastData) : FilteredWeatherForecastData{
        return FilteredWeatherForecastData(weatherForecastData.location,weatherForecastData.minTemp,weatherForecastData.maxTemp)
    }
}