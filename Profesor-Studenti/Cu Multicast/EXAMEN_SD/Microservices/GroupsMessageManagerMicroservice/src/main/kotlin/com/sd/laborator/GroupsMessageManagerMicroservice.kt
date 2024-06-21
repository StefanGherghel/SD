package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GroupsMessageManagerMicroservice {
    private val subscribers: HashMap<Int, Socket>
    private val subscriptions: HashMap<String, HashMap<Int, Socket>>
    private lateinit var messageManagerSocket: ServerSocket

    companion object Constants {
        const val GROUP_MESSAGE_MANAGER_PORT = 2500
    }
    private val mutex = Mutex()
    init {
        subscriptions = hashMapOf()
        subscribers = hashMapOf()
    }

    private suspend fun sendMessage(message: String, except: Int) {
        mutex.withLock {
            subscribers.forEach {
                it.takeIf { it.key != except }
                        ?.value?.getOutputStream()?.write((message + "\n").toByteArray())
            }
        }

        val message_processed = message.split("__~__")
        mutex.withLock {
            subscriptions.forEach { (group_name, subscribers) ->
                if (group_name == message_processed[0])
                    subscribers.forEach {
                        it.takeIf { it.key != except }
                            ?.value?.getOutputStream()?.write((message_processed[1] + "\n").toByteArray())
                    }
            }
        }
    }

    public fun run() = runBlocking{
        messageManagerSocket = ServerSocket(GROUP_MESSAGE_MANAGER_PORT)

        println("GroupsMessageManagerMicroservice Teacher se executa pe portul: ${messageManagerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        questionsSocketThread(messageManagerSocket)
    }


    private fun questionsSocketThread(socket: ServerSocket ) = runBlocking {
        while (true) {
            val clientConnection = socket.accept()

            launch(Dispatchers.Default) {
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                synchronized(subscribers) {
                    subscribers[clientConnection.port] = clientConnection
                }



                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                while (true) {
                    val receivedMessage = bufferReader.readLine()

                    if (receivedMessage == null) {
                        println("Subscriber-ul ${clientConnection.port} a fost deconectat.")
                        synchronized(subscribers) {
                            subscribers.remove(clientConnection.port)
                        }
                        bufferReader.close()
                        clientConnection.close()
                        break
                    }

                    println("[THREAD ${Thread.currentThread().name}] -> Primit mesaj: $receivedMessage")
                    sendMessage("${clientConnection.port} $receivedMessage", except = clientConnection.port)
                }
            }
        }
    }

}

fun main(args: Array<String>){
    val messageManagerMicroservice = GroupsMessageManagerMicroservice()
    messageManagerMicroservice.run()
}

