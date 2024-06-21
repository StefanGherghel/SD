package org.example

fun main(){
    val socket3rdParty = Socket3rdParty("brmu7ovrh5r90ebn6jrg")
    val localSocket = SocketLocal(8888)
    val data = socket3rdParty.getData()
    data.forEach {
        Thread.sleep(5000)
        localSocket.send(it.toString())
    }
    localSocket.closeSockets()
}