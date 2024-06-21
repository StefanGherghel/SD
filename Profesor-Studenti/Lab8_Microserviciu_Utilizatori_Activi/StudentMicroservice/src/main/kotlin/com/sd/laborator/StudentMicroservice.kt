package com.sd.laborator

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.system.exitProcess
import kotlinx.coroutines.*

class StudentMicroservice {
    // intrebarile si raspunsurile sunt mentinute intr-o lista de perechi de forma:
    // [<INTREBARE 1, RASPUNS 1>, <INTREBARE 2, RASPUNS 2>, ... ]
    private lateinit var questionDatabase: MutableList<Pair<String, String>>
    private lateinit var activeUsersManagerSocket: Socket
    private lateinit var studentServerSocket: ServerSocket

    init {
        val databaseLines: List<String> = File("questions_database.txt").readLines()
        questionDatabase = mutableListOf()

        /*
         "baza de date" cu intrebari si raspunsuri este de forma:

         <INTREBARE_1>\n
         <RASPUNS_INTREBARE_1>\n
         <INTREBARE_2>\n
         <RASPUNS_INTREBARE_2>\n
         ...
         */
        for (i in 0..(databaseLines.size - 1) step 2) {
            questionDatabase.add(Pair(databaseLines[i], databaseLines[i + 1]))
        }
    }

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val ACTIVE_USERS_MANAGER_HOST = System.getenv("ACTIVE_USERS_MANAGER_PORT") ?: "localhost"
        const val ACTIVE_USERS_MANAGER_PORT = 1500
        val STUDENT_PORT : Int = Random(java.util.Calendar.getInstance().time.time).nextInt(65536 - 1234) + 1234
    }

    private fun subscribeToMessageManager() {
        try {
            activeUsersManagerSocket = Socket(ACTIVE_USERS_MANAGER_HOST, ACTIVE_USERS_MANAGER_PORT)
            activeUsersManagerSocket.getOutputStream().write("INREGISTRARE ${studentServerSocket.inetAddress.hostAddress} ${STUDENT_PORT}\n".toByteArray())
            println("M-am conectat la ACTIVE_USERS_MANAGER!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la ACTIVE_USERS_MANAGER!")
            exitProcess(1)
        }
    }

    private fun respondToQuestion(question: String): String? {
        questionDatabase.forEach {
            // daca se gaseste raspunsul la intrebare, acesta este returnat apelantului
            if (it.first == question) {
                return it.second
            }
        }
        return null
    }

    public suspend fun run() = coroutineScope {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        studentServerSocket = ServerSocket(STUDENT_PORT)
        subscribeToMessageManager()

        println("StudentMicroservice se executa pe portul: ${studentServerSocket.localPort}")
        println("StudentMicroservice este conectat la ActiveUsersManager portul: ${activeUsersManagerSocket.localPort}")
        println("Se asteapta mesaje...")

        val clientConnection = studentServerSocket.accept()
        val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

        while (true) {
            // se asteapta intrebari trimise prin intermediarul "TeacherMicroservice"
            val response = bufferReader.readLine()

            if (response == null) {
                // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                println("Microserviciul Teacher (${clientConnection.port}) a fost oprit.")
                bufferReader.close()
                clientConnection.close()
                break
            }

            // se foloseste o corutina separata pentru tratarea intrebarii primite
            launch {
                val (messageType, messageBody) = response.split(" ", limit = 2)

                when(messageType) {
                    // tipul mesajului cunoscut de acest microserviciu este de forma:
                    // intrebare <DESTINATIE_RASPUNS> <CONTINUT_INTREBARE>
                    "INTREBARE" -> {
                        println("Am primit o intrebare de la profesor: \"${messageBody}\"")
                        var responseToQuestion = respondToQuestion(messageBody)

                        if (responseToQuestion == null)
                        {
                            clientConnection.getOutputStream().write(("Studentul nu stie" + "\n").toByteArray())
                        }
                        else
                        {
                            responseToQuestion = "RASPUNS $responseToQuestion"
                            println("Trimit raspunsul: \"${responseToQuestion}\"")
                            clientConnection.getOutputStream().write((responseToQuestion + "\n").toByteArray())
                        }
                    }
                }
            }
        }
    }
}

suspend fun main(args: Array<String>) = coroutineScope {
    val studentMicroservice = StudentMicroservice()
    studentMicroservice.run()
}