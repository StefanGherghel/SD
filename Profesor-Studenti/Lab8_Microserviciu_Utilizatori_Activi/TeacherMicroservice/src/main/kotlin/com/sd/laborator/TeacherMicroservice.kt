package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess
import kotlinx.coroutines.*

class TeacherMicroservice {
    private lateinit var activeUsersManagerSocket: Socket
    private lateinit var student: Socket
    private lateinit var teacherMicroserviceServerSocket: ServerSocket

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val ACTIVE_USERS_MANAGER_HOST = System.getenv("ACTIVE_USERS_MANAGER_PORT") ?: "localhost"
        const val ACTIVE_USERS_MANAGER_PORT = 1500
        const val TEACHER_PORT = 1600
        var connected = false
    }

    private fun subscribeToMessageManager() {
        try {
            activeUsersManagerSocket = Socket(ACTIVE_USERS_MANAGER_HOST, ACTIVE_USERS_MANAGER_PORT)
            println("M-am conectat la ACTIVE_USERS_MANAGER!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la ACTIVE_USERS_MANAGER!")
            exitProcess(1)
        }
    }

    public suspend fun run() = coroutineScope {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        subscribeToMessageManager()

        // se porneste un socket server TCP pe portul 1600 care asculta pentru conexiuni
        teacherMicroserviceServerSocket = ServerSocket(TEACHER_PORT)

        println("TeacherMicroservice se executa pe portul: ${teacherMicroserviceServerSocket.localPort}")
        println("Se asteapta cereri (intrebari)...")

        while (true) {
            // se asteapta conexiuni din partea clientilor ce doresc sa puna o intrebare
            // (in acest caz, din partea aplicatiei client GUI)
            val clientConnection = teacherMicroserviceServerSocket.accept()

            // se foloseste un thread separat pentru tratarea fiecarei conexiuni client
            launch {
                println("S-a primit o cerere de la: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // se citeste intrebarea dorita
                val clientBufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                val receivedQuestion = clientBufferReader.readLine()

                // Inchide socket-ul studentului la iesire
                if (receivedQuestion == "EXIT" && connected)
                {
                    if(!student.isClosed) {
                        student.close()
                        connected = false
                        clientConnection.getOutputStream().write("M-am deconectat de la student\n".toByteArray())
                    }
                    return@launch
                }

                // Daca e conectat atunci ii trimite mesaje doar studentului
                if (connected) {

                    if(!receivedQuestion.contains("INTREBARE"))
                    {
                        clientConnection.getOutputStream().write("Reformulati intrebarea\n".toByteArray())
                        print("Reformulati intrebarea")
                        return@launch
                    }


                    launch {
                        student.getOutputStream().write((receivedQuestion + "\n").toByteArray())
                        val bufferReader = BufferedReader(InputStreamReader(student.inputStream))
                        val receivedMessage = bufferReader.readLine()

                        if (receivedMessage == null) {
                            println("Student-ul ${student.port} a fost deconectat.")
                            bufferReader.close()
                            student.close()
                            connected = false
                        }

                        clientConnection.getOutputStream().write("$receivedMessage\n".toByteArray())
                    }
                    return@launch
                }

                // Daca studentul nu e conectat se intra aici si se executa mai departe
                try {
                    val (messageType, IP, Port) = receivedQuestion.split(" ", limit = 3)
                }
                catch (e : Exception)
                {
                    clientConnection.getOutputStream().write("Eroare la spargerea mesajului\n".toByteArray())
                    return@launch
                }

                val (messageType, IP, Port) = receivedQuestion.split(" ", limit = 3)

                if(!messageType.equals("VERIFICARE") && !connected)
                {
                    clientConnection.getOutputStream().write("Verificati conexiunea cu un student\n".toByteArray())
                    return@launch
                }


                println("Trimit catre ACTIVE_USERS_MANAGER: ${"$messageType $IP $Port\n"}")
                activeUsersManagerSocket.getOutputStream().write(("$messageType $IP $Port\n").toByteArray())

                // se asteapta raspuns de la MessageManager
                val activeUsersManagerBufferReader = BufferedReader(InputStreamReader(activeUsersManagerSocket.inputStream))
                try {
                    val receivedResponse = activeUsersManagerBufferReader.readLine()

                    // se trimite raspunsul inapoi clientului apelant
                    println("Am primit raspunsul: \"$receivedResponse\"")

                    if(receivedResponse.equals("EXISTA"))
                    {
                        println("Ma conectez la: $IP:$Port...")
                        clientConnection.getOutputStream().write("Ma conectez la: $IP:$Port...\n".toByteArray())
                        student = Socket(IP, Port.toInt())
                        connected = true
                    }

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

public suspend fun main(args: Array<String>) = coroutineScope {

    val teacherMicroservice = TeacherMicroservice()
    teacherMicroservice.run()
}