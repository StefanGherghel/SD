package com.sd.laborator

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class TeacherMicroservice {
    private lateinit var messageManagerSocket: Socket
    private lateinit var filterSocket: Socket
    private lateinit var teacherMicroserviceServerSocket: ServerSocket

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        val FILTER_HOST = System.getenv("FILTER_HOST") ?: "localhost"
        const val FILTER_PORT = 1700
        const val MESSAGE_MANAGER_PORT = 1500
        const val TEACHER_PORT = 1600
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            messageManagerSocket.soTimeout = 3000
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    private fun subscribeToFilter(){
        try {
            filterSocket = Socket(FILTER_HOST, FILTER_PORT)
            println("M-am conectat la Filter!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la Filter!")
            exitProcess(1)
        }
    }

    public fun run() {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        subscribeToMessageManager()
        subscribeToFilter()

        // se porneste un socket server TCP pe portul 1600 care asculta pentru conexiuni
        teacherMicroserviceServerSocket = ServerSocket(TEACHER_PORT)

        println("TeacherMicroservice se executa pe portul: ${teacherMicroserviceServerSocket.localPort}")
        println("Se asteapta cereri (intrebari)...")

        while (true) {
            // se asteapta conexiuni din partea clientilor ce doresc sa puna o intrebare
            // (in acest caz, din partea aplicatiei client GUI)
            val clientConnection = teacherMicroserviceServerSocket.accept()
            thread {

                println("S-a primit o cerere de la: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // se citeste intrebarea dorita
                val clientBufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                val receivedQuestion = clientBufferReader.readLine()

                // modific tipul intrebarii
                // broadcast intrebare -> trimte la toti
                // selected#numar intrebare -> trimite la primii numar studenti

                val messageType = receivedQuestion.split(" ")[0]
                val messageContent = receivedQuestion.subSequence(messageType.length + 1, receivedQuestion.length)

                // bufferul message manager
                val messageManagerBufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
                val filterBufferedReader = BufferedReader(InputStreamReader(filterSocket.inputStream))

                if (messageType.startsWith("broadcast")) {
                    // intrebarea este redirectionata catre microserviciul MessageManager
                    println("Trimit catre MessageManager: ${"intrebare ${messageManagerSocket.localPort} $messageContent\n"}")
                    messageManagerSocket.getOutputStream()
                        .write(("intrebare ${messageManagerSocket.localPort} $messageContent\n").toByteArray())
                } else {
                    if (messageType.startsWith("selected")) {

                        // trimit o cerere de numarul de student de la filter

                        println("Trimit catre Filter: ${"studenti ${messageManagerSocket.localPort} none\n"}")
                        filterSocket.getOutputStream()
                            .write(("studenti ${messageManagerSocket.localPort} none\n").toByteArray())

                        // astept lista de studenti
                        val receivedResponse = filterBufferedReader.readLine()
                        // se trimite raspunsul inapoi clientului apelant
                        println("Am primit lista: \"$receivedResponse\"")

                        // parsez lista
                        val studentList = receivedResponse.split("/")
                            .filter { it -> !it.isEmpty() }
                            .map { it -> it.toInt() }

                        // voi selecta un nr de studenti studenti
                        val number = messageType.split("#")[1].toInt()

                        if (studentList.size <= number) {
                            // simple broadcast
                            // intrebarea este redirectionata catre microserviciul MessageManager
                            println("Trimit catre MessageManager: ${"intrebare ${messageManagerSocket.localPort} $messageContent\n"}")
                            messageManagerSocket.getOutputStream()
                                .write(("intrebare ${messageManagerSocket.localPort} $messageContent\n").toByteArray())
                        } else {
                            // select 2 random students
                            val selected = studentList.shuffled().subList(0, number + 1)
                            val messageString = selected.map { it -> it.toString() }
                                .reduce { acc, it ->
                                    "$it/"
                                }
                            // selected broadcast
                            // intrebarea este redirectionata catre microserviciul MessageManager
                            println("Trimit catre MessageManager: ${"broadcast ${messageString} $messageContent\n"}")
                            messageManagerSocket.getOutputStream()
                                .write(("broadcast ${messageString} $messageContent\n").toByteArray())
                        }
                    } else {
                        // continut eronat
                    }
                }

                // se asteapta raspuns de la MessageManager
                try {
                    val receivedResponse = messageManagerBufferReader.readLine()

                    // se trimite raspunsul inapoi clientului apelant
                    println("Am primit raspunsul: \"$receivedResponse\"")
                    clientConnection.getOutputStream().write((receivedResponse + "\n").toByteArray())
                } catch (e: SocketTimeoutException) {
                    println("Nu a venit niciun raspuns in timp util.")
                    clientConnection.getOutputStream().write("Nu a raspuns nimeni la intrebare\n".toByteArray())
                } finally {
                    // se inchide conexiunea cu clientul
                    clientConnection.close()
                }

            }
        }
    }
}

fun main(args: Array<String>) {
    val teacherMicroservice = TeacherMicroservice()
    teacherMicroservice.run()
}