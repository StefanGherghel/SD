package com.sd.laborator

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class FilterMessageMicroservice {
    private lateinit var messageManagerSocket: Socket
    private lateinit var filterManagerSocket: ServerSocket

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"

        const val MESSAGE_MANAGER_PORT = 1500
        const val FILTER_PORT = 1700
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    private fun handleClient(clientConnection:Socket){
        val clientBufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
        while(true) {
            // se citeste intrebarea dorita
            val receivedQuestion = clientBufferReader.readLine()

            if( receivedQuestion.isEmpty() ){
                println("Clientul ${clientConnection.port} s-a deconectat!")
                break
            }

            println("S-a primit o cerere de la: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

            // modific tipul intrebarii
            // broadcast intrebare -> trimte la toti
            // selected#numar intrebare -> trimite la primii numar studenti

            val messageType = receivedQuestion.split(" ")[0]

            // bufferul message manager
            val messageManagerBufferReader =
                BufferedReader(InputStreamReader(messageManagerSocket.inputStream))

            if (messageType.startsWith("studenti")) {

                // intrebare pentru studenti selectati
                // voi afla lista de studenti existenta si voi selecta n aleator
                // intrebarea este redirectionata catre microserviciul MessageManager
                println("Trimit catre MessageManager: ${"studenti ${filterManagerSocket.localPort} none\n"}")
                messageManagerSocket.getOutputStream()
                    .write(("studenti ${filterManagerSocket.localPort} none\n").toByteArray())

                // astept lista de studenti
                val receivedResponse = messageManagerBufferReader.readLine()
                // se trimite raspunsul inapoi clientului apelant
                println("Am primit lista: \"$receivedResponse\"")

                // Trimit lista spre Teacher
                clientConnection.getOutputStream()
                    .write((receivedResponse + "\n").toByteArray())
            }
        }
    }

    fun run(){
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        subscribeToMessageManager()
        filterManagerSocket = ServerSocket(FILTER_PORT)

        println("FilterMicroservice se executa pe portul: ${filterManagerSocket.localPort}")
        println("Se asteapta cereri (studenti)...")

        while (true) {
            // se asteapta conexiuni din partea clientilor ce doresc sa puna o intrebare
            // (in acest caz, din partea aplicatiei client GUI)
            val clientConnection = filterManagerSocket.accept()
            println("Subscriber connected: ${clientConnection.port}")
            thread {
                handleClient(clientConnection)
            }
        }
    }
}

fun main(args: Array<String>) {
    val filterMessageMicroservice = FilterMessageMicroservice()
    filterMessageMicroservice.run()
}