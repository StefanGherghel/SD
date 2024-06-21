package com.sd.laborator.controllers

import com.sd.laborator.GradientDescent
import com.sd.laborator.model.Point
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import kotlin.math.atan

@Controller
class GetData {
    @Autowired
    private lateinit var gradientDescent: GradientDescent

    @RabbitListener(queues = ["\${regression.rabbitmq.queue}"])
    fun receiveMessage(msg: String) {
        val mappingData = msg.split("\n").flatMap { it.split("-") }.filter {
        it != "" }.map { it.toDouble() }.zipWithNext().map { Point(it.first, it.second) }
        val (x, y, mSE) = gradientDescent.applyGradient(mappingData, 0.01, 10000, 1e-6)
        println("x = $x, y = $y, mse = $mSE")
        println("Angle: atan(x) = ${atan(x)}")
    }
}