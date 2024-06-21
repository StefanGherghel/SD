package org.example
import khttp.get
import org.json.JSONObject

class Socket3rdParty(token: String) {
    private val apiUrl = "https://finnhub.io/api/v1/"
    private val pathSymbols = "stock/symbol?exchange=US&token=$token"
    private val pathPrice = "stock/price-target?&token=$token"

    fun getData(): List<JSONObject> {
        return get("$apiUrl$pathSymbols").jsonArray
            .map { JSONObject(it.toString()) }
    }

    fun getSpecificData(symbol: String):List<JSONObject>{
        return try {
            get("$apiUrl$pathPrice&symbol=$symbol")
                .jsonArray
                .map {
                    JSONObject(it.toString())
                }
        }
        catch (e: Throwable){
            listOf()
        }
    }
}