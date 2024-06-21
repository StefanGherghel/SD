package com.sd.laborator.controllers

import com.sd.laborator.GetWeatherData
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
class CitiesFilter {
    @Autowired
    private lateinit var getWeatherData: GetWeatherData
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

    @RabbitListener(queues = ["\${regression.rabbitmq.queue}"])
    fun receiveMessage(msg: String) {
        // Process cities
        val msg2 = msg.substring(1, msg.length - 1)
        val cities = msg2.split(", ")

        // Get Data for every city
        val data = getWeatherData.generateData(cities)

        // Send data
        val stringBuilder = StringBuilder()
        data.forEach {
            stringBuilder.append("${it.value.temp}-${it.value.humidity}\n")
        }
        sendMessage(stringBuilder.toString())
    }

}