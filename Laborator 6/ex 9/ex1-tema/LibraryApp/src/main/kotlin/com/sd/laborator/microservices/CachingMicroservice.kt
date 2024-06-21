package com.sd.laborator.microservices

import com.sd.laborator.controllers.RabbitMqController
import com.sd.laborator.interfaces.CacheDAO
import com.sd.laborator.interfaces.LibraryDAO
import com.sd.laborator.model.Cache
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
class CachingMicroservice {
    @Autowired
    private lateinit var cachingDAO: CacheDAO
    @Autowired
    private lateinit var rabbitMqController: RabbitMqController
    @Autowired
    private lateinit var amqpTemplate: AmqpTemplate

    @RabbitListener(queues = ["\${libraryapp.rabbitmq.queue1}"])
    fun fetchMessage(message: String) {
        val processed_msg = message.split("_")
        println("cache")
        val ts = System.currentTimeMillis() / 60000
        val result: String? = when(processed_msg[0]) {

            "print" -> {
                processed_msg[1]
                val result=cachingDAO.findByQuery(message)
                if(result.isEmpty())
                {
                    "not exists_"+message
                }
                else
                {
                    if(ts-(result.elementAt(0)?.cacheTimestamp!!)<=60)
                    {
                        "exists_"+ (result.elementAt(0)?.cacheResult ?: "")
                    }
                    else
                    {
                        "not exists_"+message
                    }
                }
            }
            "find-and-print" -> {
                val format=processed_msg[1]
                var result=cachingDAO.findByQuery(message)
                if(result.isEmpty())
                {
                    "not exists_"+message
                }
                else
                {
                    if(ts-(result.elementAt(0)?.cacheTimestamp!!)<=60)
                    {
                        "exists_"+ (result.elementAt(0)?.cacheResult ?: "")
                    }
                    else
                    {
                        "not exists_"+message
                    }
                }
            }
            "add"->{
                if(processed_msg[1] == "print") {
                    val querry = processed_msg[1] + "_" + processed_msg[2]
                    val content = processed_msg[3]
                    cachingDAO.addCache(Cache(ts.toInt(), querry, content))
                    null
                } else {
                    val querry = processed_msg[1] + "_" + processed_msg[2] + "_" + processed_msg[3]
                    val content = processed_msg[4]
                    cachingDAO.addCache(Cache(ts.toInt(), querry, content))
                    null
                }
            }
            else -> null

        }
        println("result: ")
        println(result)
        if (result != null) sendMessage(result)
    }

    fun sendMessage(message: String) {
        println("message: ")
        println(message)
        rabbitMqController.setRoutingKey("libraryapp.routingkey")
        this.amqpTemplate.convertAndSend(rabbitMqController.getExchange(),
            rabbitMqController.getRoutingKey(),
            message)
    }

}