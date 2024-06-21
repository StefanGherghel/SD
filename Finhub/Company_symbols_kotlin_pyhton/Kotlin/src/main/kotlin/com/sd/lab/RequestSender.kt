package com.sd.lab

import khttp.get
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.lang.Thread.sleep
import java.net.ServerSocket
import java.text.SimpleDateFormat
import java.util.*

class NewsSender(private val API_key: String) {
    private lateinit var serverSocket: ServerSocket

    private var ssg: StockSymbolsGenerator
    private var cng: CompanyNewsGenerator

    companion object Constants {
        val HOST = "127.0.0.1"
        val PORT = 65432
    }

    fun run(){
        serverSocket = ServerSocket(PORT)
        println("Se asteapta conexiuni...")

        val clientConnection = serverSocket.accept()
        for(company in ssg.getStockSymbolsAsJson().jsonArray){
            val news = cng.getNewsAsJson(company.jsonObject.getValue("symbol").toString().replace("\"", ""))
            if(news.toString().length > 2) {
                println(news)

                //se trimite prima stire
                clientConnection.getOutputStream()?.write((news.jsonArray[0].toString() + "\n").toByteArray())

            }
            else
                println("Nu sunt stiri pentru " + company.jsonObject.getValue("description").toString())

            sleep(1000)
        }
    }

    init {
        ssg = StockSymbolsGenerator(API_key)
        cng = CompanyNewsGenerator(API_key)
    }

}

class StockSymbolsGenerator(private val API_key: String){
    fun getStockSymbolsAsJson(): JsonElement {
        val link = "https://finnhub.io/api/v1/stock/symbol?exchange=US&token=" + API_key
        val companySymbolsRequest = get(link)
        val data = companySymbolsRequest.content.decodeToString()

        return Json.parseToJsonElement(data)
    }
}

class CompanyNewsGenerator(private val API_key: String){
    fun getNewsAsJson(companySymbol: String): JsonElement {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDate = sdf.format(Date()).split(" ")[0]

        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        val yesterday = sdf.format(cal.getTime()).split(" ")[0]

        val link = "https://finnhub.io/api/v1/company-news?symbol=" + companySymbol + "&from=" + yesterday + "&to=" + currentDate + "&token=" + API_key

        val companyNewsRequest = get(link)
        val data = companyNewsRequest.content.decodeToString()

        return Json.parseToJsonElement(data)
    }
}

fun main(){
    //se rulleaza un server soccket si se asteapta sa se conecteze un client, i se trimit datele, apoi se inchide
    //ATENTIE: nu e facut sa ruleze pt mai multi clienti(nu se cerea in enunt explicit)
    val ns = NewsSender("c38sivqad3ido5ak9ibg")
    ns.run()
}