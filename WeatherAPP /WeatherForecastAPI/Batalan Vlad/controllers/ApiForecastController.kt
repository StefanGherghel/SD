package com.sd.laborator.controllers

import com.sd.laborator.interfaces.OrchestratorServiceInterface
import com.sd.laborator.interfaces.ReplicationServiceInterface
import com.sd.laborator.interfaces.WeatherAppVisualisationInterface
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URL

@Controller
class ApiForecastController {

    // iar aici putem folosi serviciul pe care l am facut
    @Autowired
    private lateinit var weatherAppVisualisationService: WeatherAppVisualisationInterface

    // Asta se cheama mangleala
    // Cand sari peste pasi si asumi ca sunt facuti corect doar ca sa nu pierzi timp
    @Autowired
    private lateinit var replicationService: ReplicationServiceInterface

    @RequestMapping("/vremea/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable location: String): String {

        // We replicate the service with ease
        replicationService.replicateService(3)

        return weatherAppVisualisationService.getForecastData(location)
    }

}