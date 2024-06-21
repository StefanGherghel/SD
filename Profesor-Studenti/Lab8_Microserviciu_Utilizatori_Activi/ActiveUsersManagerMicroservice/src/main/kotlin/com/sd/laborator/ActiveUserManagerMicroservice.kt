package com.sd.laborator
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlinx.coroutines.*

class ActiveUsersManagerMicroservice {

    private val users: HashMap<Int, Socket>
    private val usersServers: HashMap<Int, String>
    private lateinit var activeUsersManagerServerSocket: ServerSocket

    init {
        users = hashMapOf()
        usersServers = hashMapOf()
    }

    companion object Constants {
            const val ACTIVE_USERS_MANAGER_PORT = 1500
    }

    private fun checkIfUsersExists(IP : String, Port : Int) : Boolean {
        return usersServers.containsKey(Port) && usersServers[Port].equals(IP)
    }

    public suspend fun run() = coroutineScope {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        activeUsersManagerServerSocket = ServerSocket(ACTIVE_USERS_MANAGER_PORT)
        println("ActiveUsersManagerMicroservice se executa pe portul: ${activeUsersManagerServerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (true) {
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = activeUsersManagerServerSocket.accept()

            // se porneste o corutina separat pentru tratarea conexiunii cu clientul
            launch {
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // adaugarea in lista de subscriberi trebuie sa fie atomica!
                synchronized(users) {
                    users[clientConnection.port] = clientConnection
                }

                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                while (true) {
                    // se citeste raspunsul de pe socketul TCP
                    val receivedMessage = bufferReader.readLine()

                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if (receivedMessage == null) {
                        // deci subscriber-ul respectiv a fost deconectat
                        println("Subscriber-ul ${clientConnection.port} a fost deconectat.")
                        synchronized(users) {
                            users.remove(clientConnection.port)
                        }
                        bufferReader.close()
                        clientConnection.close()
                        break
                    }

                    println("Primit mesaj: $receivedMessage")
                    val (messageType, IP, Port) = receivedMessage.split(" ", limit = 3)

                    when (messageType) {
                        "VERIFICARE" -> {
                            if(checkIfUsersExists(IP, Port.toInt()))
                            {
                                clientConnection.getOutputStream().write("EXISTA\n".toByteArray())
                            }
                            else
                            {
                                clientConnection.getOutputStream().write("NU EXISTA\n".toByteArray())
                            }
                        }
                        "INREGISTRARE" -> {
                            synchronized(usersServers) {
                                usersServers[Port.toInt()] = IP
                                println("User $Port inregistrat")
                            }
                        }
                    }
                }
            }
        }
    }

}



public suspend fun main(args: Array<String>) = coroutineScope {
    val activeUsersManagerMicroservice = ActiveUsersManagerMicroservice()
    activeUsersManagerMicroservice.run()
}

