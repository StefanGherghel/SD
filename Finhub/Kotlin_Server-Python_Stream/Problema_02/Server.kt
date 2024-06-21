package org.example

fun main(){
    val socket3rdParty = Socket3rdParty("brl7eb7rh5re1lvco7fg")
    val localSocket = SocketLocal(8888)
    val data = socket3rdParty.getData()
    data.forEach {
        val items = socket3rdParty.getSpecificData(it.get("symbol").toString())
        if(items.isNotEmpty()){
            items.forEach { data->
                Thread.sleep(3000)
                print("n$data\n")
                localSocket.send(data.toString())
            }
        }
    }
    localSocket.closeSockets()
}