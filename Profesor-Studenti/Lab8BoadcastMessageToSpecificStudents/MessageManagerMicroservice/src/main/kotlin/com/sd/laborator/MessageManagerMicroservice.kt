package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class MessageManagerMicroservice {
    private val subscribers: HashMap<Int, Socket>
    private val students: MutableList<Int>
    private lateinit var messageManagerSocket: ServerSocket

    companion object Constants {
        const val MESSAGE_MANAGER_PORT = 1500
        const val FILTER_PORT = 1700
    }

    init {
        subscribers = hashMapOf()
        students = mutableListOf()
    }

    private fun broadcastMessage(message: String, except: Int) {
        subscribers.forEach {
            it.takeIf { it.key != except && it.key in students}
                ?.value?.getOutputStream()?.write((message + "\n").toByteArray())
        }
    }

    // ############## MODIFICARE PENTRU A GESTIONA O INTREBARE PT GRUP SELECT DE STUDENTI ##########
    private fun broadcastSelectiveMessage(source:Int, destinations: String, messageBody: String) {
        val destinationPorts = destinations.split("/")
        for( destPort in destinationPorts ){
            if( !destPort.isEmpty() ){
                try{
                    val port = destPort.toInt()
                    if( port in students ){
                        // create intrebare message from student
                        val message = "intrebare ${source} $messageBody"
                        subscribers[port]?.getOutputStream()?.write((message+"\n").toByteArray())
                    }
                }
                catch (ex:Exception){
                    continue
                }
            }
        }
    }

    private fun respondTo(destination: Int, message: String) {
        subscribers[destination]?.getOutputStream()?.write((message + "\n").toByteArray())
    }


    // formatul o sa fie:
    // port1/port2/port3/
    private fun getStudentList(destination: Int) {
        var studentList = StringBuilder()
        students.forEach{
            studentList.append("$it/")
        }
        val message = studentList.toString()
        println("Generated the list: $message")
        subscribers[destination]?.getOutputStream()?.write((message + "\n").toByteArray())
        println("Sent the list to $destination")
    }

    private fun handleClient(clientConnection: Socket){
        println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

        // adaugarea in lista de subscriberi trebuie sa fie atomica!
        synchronized(subscribers) {
            subscribers[clientConnection.port] = clientConnection
            val subs = subscribers.map { it -> it.value.port.toString() }.reduce{
                acc, s->
                acc + "$s "
            }
            println("Current subscribers: $subs")
        }

        val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

        while (true) {
            // se citeste raspunsul de pe socketul TCP
            val receivedMessage = bufferReader.readLine()

            // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
            if (receivedMessage == null) {
                // deci subscriber-ul respectiv a fost deconectat
                println("Subscriber-ul ${clientConnection.port} a fost deconectat.")

                synchronized(subscribers) {
                    subscribers.remove(clientConnection.port)
                }

                synchronized(students) {
                    if (clientConnection.port in students) {
                        students.remove(clientConnection.port)
                    }
                }
                bufferReader.close()
                clientConnection.close()
                break
            }

            println("Primit mesaj: $receivedMessage")
            val (messageType, messageDestination, messageBody) = receivedMessage.split(" ", limit = 3)

            when (messageType) {
                "intrebare" -> {
                    // tipul mesajului de tip intrebare este de forma:
                    // intrebare <DESTINATIE_RASPUNS> <CONTINUT_INTREBARE>

                    broadcastMessage(
                        "intrebare ${clientConnection.port} $messageBody",
                        except = clientConnection.port
                    )
                }
                "raspuns" -> {
                    // tipul mesajului de tip raspuns este de forma:
                    // raspuns <CONTINUT_RASPUNS>
                    respondTo(messageDestination.toInt(), messageBody)
                }
                "identitate" -> {
                    if(messageBody == "student"){
                        // verific daca portul nu este deja in lista de studenti

                        synchronized(students) {
                            if (!students.contains(clientConnection.port)) {
                                students.add(clientConnection.port)
                            }
                        }
                    }
                }
                "studenti" -> {
                    // profesorul a trimis pentru a primi lista de studenti din clasa
                    // create string to send
                    getStudentList(clientConnection.port)
                }

                // a se adauga posibilitatea de intrebare subgrup studenti
                "broadcast" -> {
                    // va fi trimis de profesor cand doreste sa trimita doar unui grup
                    // broadcast port1/port2/port3/message intrebare
                    broadcastSelectiveMessage(clientConnection.port, messageDestination, messageBody)
                }
            }
        }
    }

    public fun run() {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        messageManagerSocket = ServerSocket(MESSAGE_MANAGER_PORT)
        println("MessageManagerMicroservice se executa pe portul: ${messageManagerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (true) {
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = messageManagerSocket.accept()

            println("${clientConnection.port} connected before GlobalLaunch.")

            // se porneste un thread separat pentru tratarea conexiunii cu clientul

            // vom utiliza Coroutina pentru asta
            // este necesar si un run blocking pentru a executa pt ca ai in interior
            // metode blocante cum ar fi IO si altele
            // facem cruce mare

            // Asta e tot ce e leget de coroutine <3
            thread {
                handleClient(clientConnection)
            }
        }
    }
}

fun main(args: Array<String>) {
    val messageManagerMicroservice = MessageManagerMicroservice()
    messageManagerMicroservice.run()
}

