package org.example

import java.net.ServerSocket

class LocalSocket(port: Int) {
    private val localSocket = ServerSocket(port)
    private val pySparkSocket = localSocket.accept()

    fun sendData(data: String){
        println(data)
        pySparkSocket.getOutputStream().write("$data\n".toByteArray())
    }

    fun closeSocket(){
        pySparkSocket.close()
        localSocket.close()
    }
}