package com.sd.laborator.services

import com.sd.laborator.interfaces.CityListInterface
import com.sd.laborator.interfaces.LiniarRegressionInterface
import com.sd.laborator.interfaces.LocationSearchInterface
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.stereotype.Service
import java.net.URL
import org.json.JSONObject
import java.io.File
import java.lang.Math.atan
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class LiniarRegressionService : LiniarRegressionInterface {
    override fun getRegressionAngle(forecastList: List<WeatherForecastData>): Double {
        // Apply filters bla bla
        // Humidity not null filter
        // MatTemp not null filter

        // Evaluate regression over Humidity and MaxTemp
        // 1 - find means
        var meanX = 0.0
        forecastList.forEach { it -> meanX += it.humidity!! }
        meanX /= forecastList.size

        var meanY = 0.0
        forecastList.forEach { it -> meanY += it.maxTemp!! }
        meanY /= forecastList.size

        // 2 - find variance
        var varianceX = 0.0
        forecastList.forEach { it -> varianceX += (it.humidity!! - meanX) * (it.humidity!! - meanX) }

        // 3 - find covariance
        var covariance = 0.0
        forecastList.forEach { it -> covariance += (it.humidity!! - meanX) * (it.maxTemp!! - meanY) }

        // 4 - get angle tangent coefficient
        val a = covariance/varianceX

        // 5 - get angle value
        val angle = atan(a.toDouble())

        return angle
    }
}