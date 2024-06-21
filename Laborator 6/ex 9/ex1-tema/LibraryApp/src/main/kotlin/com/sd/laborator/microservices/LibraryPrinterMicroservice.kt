package com.sd.laborator.microservices

import com.sd.laborator.controllers.RabbitMqController
import com.sd.laborator.interfaces.LibraryDAO
import com.sd.laborator.interfaces.LibraryPrinter
import com.sd.laborator.model.Book
import com.sd.laborator.model.Cache
import com.sd.laborator.model.Content
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.lang.Thread.sleep
import java.util.*

@Controller
class LibraryPrinterMicroservice {
    @Autowired
    private lateinit var libraryDAO: LibraryDAO

    @Autowired
    private lateinit var libraryPrinter: LibraryPrinter
    @Autowired
    private lateinit var rabbitMqController: RabbitMqController
    @Autowired
    private lateinit var amqpTemplate: AmqpTemplate
    private var queue: Queue<String> = LinkedList<String>()
    @RabbitListener(queues = ["\${libraryapp.rabbitmq.queue}"])
    fun fetchMessage(message: String) {
        var processed_message=message.split('_')
        print(message)
        when(processed_message[0]) {
            "exists" -> {
                queue.add(processed_message[1])
            }
            "not exists","exists+badtimestamp" -> {
                //val querry=processed_message[1].split('_')
                val operatie=processed_message[1]
                val format=processed_message[2]
                if(operatie=="print")
                {

                    if(format=="json")
                    {
                        val data = libraryPrinter.printJSON(libraryDAO.getBooks() as Set<Book>)
                        sendMessage("add_"+operatie+"_"+format+"_"+data)
                        queue.add(data)

                    }
                    else if(format=="html")
                    {
                        val data = libraryPrinter.printHTML(libraryDAO.getBooks() as Set<Book>)
                        sendMessage("add_"+operatie+"_"+format+"_"+data)
                        queue.add(data)
                    }
                    else
                    {
                        val data = libraryPrinter.printRaw(libraryDAO.getBooks() as Set<Book>)
                        sendMessage("add_"+operatie+"_"+format+"_"+data)
                        queue.add(data)
                    }
                }
                else
                {
                    val filter=processed_message[3].split('=')
                    if(format=="json")
                    {

                        if(filter[0]=="author") {
                            var data = libraryPrinter.printJSON(libraryDAO.findAllByAuthor(filter[1]) as Set<Book>)
                            sendMessage("add_"+operatie+"_"+format+"_"+filter[0]+"="+filter[1]+"_"+data)
                            queue.add(data)
                        }
                        else if(filter[0]=="title")
                        {
                            var data = libraryPrinter.printJSON(libraryDAO.findAllByTitle(filter[1]) as Set<Book>)
                            sendMessage("add_"+operatie+"_"+format+"_"+filter[0]+"="+filter[1]+"_"+data)
                            queue.add(data)
                        }
                        else
                        {
                            var data = libraryPrinter.printJSON(libraryDAO.findAllByPublisher(filter[1]) as Set<Book>)
                            sendMessage("add_"+operatie+"_"+format+"_"+filter[0]+"="+filter[1]+"_"+data)
                            queue.add(data)
                        }

                    }
                    else if(format=="html")
                    {
                        if(filter[0]=="author") {
                            var data = libraryPrinter.printHTML(libraryDAO.findAllByAuthor(filter[1]) as Set<Book>)
                            sendMessage("add_"+operatie+"_"+format+"_"+filter[0]+"="+filter[1]+"_"+data)
                            queue.add(data)
                        }
                        else if(filter[0]=="title")
                        {
                            var data = libraryPrinter.printHTML(libraryDAO.findAllByTitle(filter[1]) as Set<Book>)
                            sendMessage("add_"+operatie+"_"+format+"_"+filter[0]+"="+filter[1]+"_"+data)
                            queue.add(data)
                        }
                        else
                        {
                            var data = libraryPrinter.printHTML(libraryDAO.findAllByPublisher(filter[1]) as Set<Book>)
                            sendMessage("add_"+operatie+"_"+format+"_"+filter[0]+"="+filter[1]+"_"+data)
                            queue.add(data)
                        }
                    }
                    else
                    {
                        if(filter[0]=="author") {
                            var data = libraryPrinter.printRaw(libraryDAO.findAllByAuthor(filter[1]) as Set<Book>)
                            sendMessage("add_"+operatie+"_"+format+"_"+filter[0]+"="+filter[1]+"_"+data)
                            queue.add(data)
                        }
                        else if(filter[0]=="title")
                        {
                            var data = libraryPrinter.printRaw(libraryDAO.findAllByTitle(filter[1]) as Set<Book>)
                            sendMessage("add_"+operatie+"_"+format+"_"+filter[0]+"="+filter[1]+"_"+data)
                            queue.add(data)
                        }
                        else
                        {
                            var data = libraryPrinter.printRaw(libraryDAO.findAllByPublisher(filter[1]) as Set<Book>)
                            sendMessage("add_"+operatie+"_"+format+"_"+filter[0]+"="+filter[1]+"_"+data)
                            queue.add(data)
                        }
                    }
                }

            }
        }
    }

    fun sendMessage(message: String) {
        println("message: ")
        println(message)
        println()
        rabbitMqController.setRoutingKey("libraryapp.routingkey1")
        this.amqpTemplate.convertAndSend(rabbitMqController.getExchange(),
            rabbitMqController.getRoutingKey(),
            message)
    }

    @RequestMapping("/print", method = [RequestMethod.GET])
    @ResponseBody
    fun customPrint(@RequestParam(required = true, name = "format", defaultValue = "") format: String): String {
        sendMessage("print_"+format)
        val thread = Thread {
                while(queue.isEmpty()) {
                    sleep(100)
                }
        }
        thread.start()
        thread.join()
        var data = queue.elementAt(0)
        queue.clear()
        return data

    }
    @RequestMapping("/find-and-print", method = [RequestMethod.GET])
    @ResponseBody
    fun customFind(@RequestParam(required = false, name = "author", defaultValue = "") author: String,
                   @RequestParam(required = false, name = "title", defaultValue = "") title: String,
                   @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String,
                   @RequestParam(required = true, name = "format", defaultValue = "") format: String): String {
        if (author != "") {
            sendMessage("find-and-print_"+format+"_author="+author)
            val thread = Thread {
                while(queue.isEmpty()) {
                    sleep(100)
                }
            }
            thread.start()
            thread.join()
            var data = queue.elementAt(0)
            queue.clear()
            return data

        }
        else if (title != "") {
            sendMessage("find-and-print_"+format+"_title="+title)
            val thread = Thread {
                while(queue.isEmpty()) {
                    sleep(100)
                }
            }
            thread.start()
            thread.join()
            var data = queue.elementAt(0)
            queue.clear()
            return data
        }
        else if (publisher != "") {
            sendMessage("find-and-print_"+format+"_publisher="+publisher)
            val thread = Thread {
                while(queue.isEmpty()) {
                    sleep(100)
                }
            }
            thread.start()
            thread.join()
            var data = queue.elementAt(0)
            queue.clear()
            return data
        }
        else {
            return "Not a valid field"
        }
    }

}