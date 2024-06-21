package com.sd.laborator.filters

import com.sd.laborator.interfaces.FilterInterface
import com.sd.laborator.pojo.WeatherForecastData

class NotNullMaxTemperatureFilter: FilterInterface {
    override fun execute(weatherForecastData: WeatherForecastData): Boolean {
        if(weatherForecastData.maxTemp == null)
            return false
        return true
    }
}