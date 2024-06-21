package com.sd.laborator

import io.micronaut.rabbitmq.annotation.Queue
import io.micronaut.rabbitmq.annotation.RabbitListener
import jakarta.inject.Inject
import java.io.File
import java.sql.Timestamp

@RabbitListener
class MessageListener {
    @Inject
    private lateinit var equationSolverService: EquationSolverService

    @Queue("micronaut.adt.queue")
    fun receive(data: String) {
        // Split in two lists
        val (leftSet, rightSet) = data.split(";")

        // Get lists back from string
        val leftSetProcessed = leftSet.substring(1, leftSet.length - 1)
        val listA = leftSetProcessed.split(",").map{ it.replace(" ", "").toInt() }

        val rightSetProcessed = leftSet.substring(1, rightSet.length - 1)
        val listB = rightSetProcessed.split(",").map {it.replace(" ", "").toInt() }

        // Get result and save to file
        val result = equationSolverService.findPairs(listA, listB)

        val newFile = File("pairs_${Timestamp(System.currentTimeMillis())}.txt")
        newFile.createNewFile()
        result.forEach {
            newFile.appendText("(${it.first},${it.second})\n")
        }
    }
}