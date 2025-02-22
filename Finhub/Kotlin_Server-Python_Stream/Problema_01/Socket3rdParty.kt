package org.example
import khttp.get
import org.json.JSONObject

class Socket3rdParty(token: String) {
    private val apiUrl = "https://finnhub.io/api/v1/"
    private val pathUrlSymbol = "stock/symbol?exchange=US&token=$token"
    private val pathUrlNews = "company-news?from=2021-06-01&to=2021-06-02&token=$token"

    fun getData(): List<JSONObject> {
        return get("$apiUrl$pathUrlSymbol").jsonArray
            .map { JSONObject(it.toString()) }
    }

    fun getSpecificData(symbol: String): List<JSONObject> {
        return try {
            get("$apiUrl$pathUrlNews&symbol=$symbol")
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