package com.sd.laborator.microservices

import com.sd.laborator.components.RabbitMqComponent
import com.sd.laborator.interfaces.BeerDAO
import com.sd.laborator.model.Beer
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BeerDAOMicroservice {
    @Autowired
    private lateinit var beerDAO: BeerDAO

    @Autowired
    private lateinit var rabbitMqComponent: RabbitMqComponent

    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = rabbitMqComponent.rabbitTemplate()
    }
    //citesc din queue1
    // scriu in queue
    @RabbitListener(queues = ["\${sql_lite_example.rabbitmq.queue2}"])
    fun recieveMessage(msg: String) {
        val processed_msg = (msg.split(",").map { it.toInt().toChar() }).joinToString(separator="")
        val (operation, parameters) = processed_msg.split('~')
        var beer: Beer? = null
        var price: Float? = null
        var name: String? = null

        // id=1;name=Corona;price=3.6
        if("id=" in parameters) {
            println(parameters)
            val params: List<String> = parameters.split(';')
            try {
                beer = Beer(
                    params[0].split('=')[1].toInt(),
                    params[1].split('=')[1],
                    params[2].split('=')[1].toFloat()
                )
            } catch (e: Exception) {
                print("Error parsing the parameters: ")
                println(params)
                return
            }
        } else if ("price=" in parameters) {
            price = parameters.split('=')[1].toFloat()
        } else if ("name=" in parameters) {
            name = parameters.split("=")[1]
        }
//        println(parameters)
//        println(name)
//        println(price)
//        println(beer)
        var result: Any? = when(operation) {
            "createBeerTable" -> beerDAO.createBeerTable()
            "addBeer" -> khttp.post("http://localhost:8080/addbeer", data = mapOf("name" to beer?.beerName, "price" to beer?.beerPrice.toString()))
            "getBeers" -> khttp.get("http://localhost:8080/getbeers").text
            "getBeerByName" -> khttp.get("http://localhost:8080/getbeerbyname/$name").text
            "getBeerByPrice" -> khttp.get("http://localhost:8080/getbeerbyprice/$price").text
            "updateBeer" -> khttp.put("http://localhost:8080/updatebeer", data = mapOf("id" to beer?.beerID.toString(), "name" to beer?.beerName, "price" to beer?.beerPrice.toString()))
            "deleteBeer" -> khttp.delete("http://localhost:8080/deletebeer/$name")
            else -> null
        }
        println("result: ")
        println(result)
        if (result != null) sendMessage(result.toString())
    }

    fun sendMessage(msg: String) {
        println("message: ")
        println(msg)
        this.amqpTemplate.convertAndSend(rabbitMqComponent.getExchange(), rabbitMqComponent.getRoutingKey(), msg)
    }

}
