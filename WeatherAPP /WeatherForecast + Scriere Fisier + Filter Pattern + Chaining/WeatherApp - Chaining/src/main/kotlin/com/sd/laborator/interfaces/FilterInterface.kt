package com.sd.laborator.interfaces

import com.sd.laborator.pojo.FilteredWeatherForecastData
import com.sd.laborator.pojo.WeatherForecastData

interface FilterInterface {
    fun filterData(weatherForecastData: WeatherForecastData?) : FilteredWeatherForecastData?
}