package com.sd.laborator.controllers

import com.sd.laborator.CitiesGenerator
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus

@Controller
class WeatherAppController {
    @Autowired
    private lateinit var citiesGenerator: CitiesGenerator
    @Autowired
    private lateinit var connectionFactory: RabbitMqConnectionFactoryComponent
    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = connectionFactory.rabbitTemplate()
    }

    private fun sendMessage(msg: String) {
        println("message: $msg")
        this.amqpTemplate.convertAndSend(connectionFactory.getExchange(), connectionFactory.getRoutingKey(), msg)
    }

    @RequestMapping("/getregression", method = [RequestMethod.PUT])
    @ResponseStatus(code = HttpStatus.OK, reason = "OK")
    fun getForecast()  {
        // Get cities from file
        val cities = citiesGenerator.readCities()
        // Send cities to microsarvis
        sendMessage(cities.toString())
    }
}