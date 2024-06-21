package com.sd.laborator.filters

import com.sd.laborator.interfaces.FilterInterface
import com.sd.laborator.pojo.WeatherForecastData

class NotNullHumidityFilter: FilterInterface {
    override fun execute(weatherForecastData: WeatherForecastData): Boolean {
        if(weatherForecastData.humidity == null)
            return false
        return true
    }
}