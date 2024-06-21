package com.sd.laborator.services

import com.sd.laborator.Filter.LocationMinMaxTempFilter
import com.sd.laborator.interfaces.FilterInterface
import com.sd.laborator.pojo.FilteredWeatherForecastData
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.stereotype.Service


@Service
class FilterService(private val dataLoggerService: DataLoggerService, private val timeService: TimeService) : FilterInterface{
    override fun filterData(weatherForecastData: WeatherForecastData?): FilteredWeatherForecastData? {
        val wishedFilter = LocationMinMaxTempFilter()
        val filteredData : FilteredWeatherForecastData?

         if (weatherForecastData != null) {
             filteredData = wishedFilter.filter(weatherForecastData)
             dataLoggerService.logData("\n[${timeService.getCurrentTime()}]Filtered data : ")
             dataLoggerService.logData(filteredData)
        } else {
             filteredData = null
        }

        return filteredData
    }
}