package org.example

fun main(){
    val socket3rdParty = Socket3rdParty("brmrfu7rh5rcss140ogg")
    val localSocket = LocalSocket(8888)
    val data = socket3rdParty.getData()
    data.forEach{
        val itemsNews = socket3rdParty.getSpecificData(it.get("symbol").toString())
        if (itemsNews.isNotEmpty()) {
            itemsNews.forEach { data ->
                Thread.sleep(3000)
                localSocket.sendData(data.toString())
            }
        }
    }
    localSocket.closeSocket()
}