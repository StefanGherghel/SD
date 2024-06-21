package org.example.microservices

import org.example.components.RabbitMqComponent
import org.example.interfaces.BeerDAO
import org.example.model.Beer
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.Exception

@Component
class BeerDAOMicroservice {
    @Autowired
    private lateinit var beerDAO: BeerDAO

    @Autowired
    private lateinit var rabbitMqComponent: RabbitMqComponent

    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate(){
        this.amqpTemplate =  rabbitMqComponent.rabbitTemplate()
    }

    // Citesc din queue1
    // Scriu in queue
    @RabbitListener(queues= ["\${sqliteexample.rabbitmq.queue}"])
    fun receiveMessage(msg: String){
        val processed_msg = (msg.split(",").map{it.toInt().toChar()}).joinToString(separator = "")
        val (operation, parameters) = processed_msg.split('~')
        var beer: Beer? = null
        var price: Float? = null
        var name: String? = null

        // id=1;name=Corona;price=3.6
        if("id=" in parameters) {
            println(parameters)
            val params: List<String> = parameters.split(";")
            try {
                beer = Beer(
                    params[0].split("=")[1].toInt(),
                    params[1].split("=")[1],
                    params[2].split("=")[1].toFloat()
                )
            }catch (e: Exception){
                print("Error parsing the parameters: ")
                println(params)
                return
            }
        } else if ("price=" in parameters){
            price = parameters.split("=")[1].toFloat()
        } else if ("name=" in parameters){
            name = parameters.split("=")[1]
        }
        println(parameters)
        println(name)
        println(price)
        println(beer)
        val result: Any? = when(operation){
            "createBeerTable" -> beerDAO.createBeerTable()
            "addBeer" -> beerDAO.addBeer(beer!!)
            "getBeers" -> beerDAO.getBeers()
            "getBeerByName" -> beerDAO.getBeerByName(name!!)
            "getBeerByPrice" -> beerDAO.getBeerByPrice(price!!)
            "updateBeer" -> beerDAO.updateBeer(beer!!)
            "deleteBeer" -> beerDAO.deleteBeer(name!!)
            else -> null
        }
        println("result: ")
        println(result)
        if(result != null) sendMessage(result.toString())
    }

    fun sendMessage(msg: String){
        println("message: ")
        println(msg)

        this.amqpTemplate.convertAndSend(rabbitMqComponent.getExchange(), rabbitMqComponent.getRoutingKey(), msg)

    }
}