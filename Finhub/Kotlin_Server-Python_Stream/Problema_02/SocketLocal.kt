package org.example

import java.net.ServerSocket

class SocketLocal(port: Int){
    private val localSocket = ServerSocket(port)
    private val pySparkSocket = localSocket.accept()

    fun send(data: String){
        println(data)
        pySparkSocket.getOutputStream().write("$data\n".toByteArray())
    }

    fun closeSockets(){
        localSocket.close()
        pySparkSocket.close()
    }
}