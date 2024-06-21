package org.example


fun main(args: Array<String>) {
    val socket3rdParty = Socket3rdParty("brmrfu7rh5rcss140ogg")
    val localSocket = SocketLocal(8888)
    val data = socket3rdParty.getData()
    data.forEach {
        val itemNews = socket3rdParty.getSpecificData(it.get("symbol").toString())
        if (itemNews.isNotEmpty()) {
            itemNews.forEach { data ->
                Thread.sleep(3000)
                localSocket.sendData(data.toString())
            }
        }
    }
    localSocket.closeSocket()
}

