package org.example.microservices

import org.example.components.RabbitMqComponent
import org.example.interfaces.*
import org.example.model.Beer
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.Exception

@Component
class BeerDAOMicroservice {
    @Autowired
    private lateinit var createBeer: CreateBeerInterface
    @Autowired
    private lateinit var getBeer: GetBeerInterface
    @Autowired
    private lateinit var updateBeer: UpdateBeerInterface
    @Autowired
    private lateinit var deleteBeer: DeleteBeerInterface

    @Autowired
    private lateinit var rabbitMqComponent: RabbitMqComponent

    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate(){
        this.amqpTemplate =  rabbitMqComponent.rabbitTemplate()
    }

    // Citesc din queue1
    // Scriu in queue
    @RabbitListener(queues = ["\${sqliteexample.rabbitmq.queue}"])
    fun receiveMessage(msg: String){
        val processedMsg = (msg.split(",").map{it.toInt().toChar()}).joinToString(separator = "")
        val (operation, parameters) = processedMsg.split('~')
        var beer: Beer? = null
        var price: Float? = null
        var name: String? = null

        // id=1;name=Corona;price=3.6
        when {
            "id=" in parameters -> {
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
            }
            "price=" in parameters -> {
                price = parameters.split("=")[1].toFloat()
            }
            "name=" in parameters -> {
                name = parameters.split("=")[1]
            }
        }
        val result: Any? = when(operation){
            "createBeerTable" -> createBeer.createBeerTable()
            "addBeer" -> createBeer.addBeer(beer!!)
            "getBeers" -> getBeer.getBeers()
            "getBeerByName" -> getBeer.getBeerByName(name!!)
            "getBeerByPrice" -> getBeer.getBeerByPrice(price!!)
            "updateBeer" -> updateBeer.updateBeer(beer!!)
            "deleteBeer" -> deleteBeer.deleteBeer(name!!)
            else -> null
        }
        if(result != null) sendMessage(result.toString())
    }

    private fun sendMessage(msg: String){
        this.amqpTemplate.convertAndSend(rabbitMqComponent.getExchange(), rabbitMqComponent.getRoutingKey(), msg)

    }
}