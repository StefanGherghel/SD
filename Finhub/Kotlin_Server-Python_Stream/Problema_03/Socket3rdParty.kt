package org.example
import khttp.get
import org.json.JSONObject

class Socket3rdParty(token: String) {
    private val apiUrl = "https://finnhub.io/api/v1/"
    private val pathNews = "company-news?symbol=AAPL&from=2021-06-06&to=2021-06-20&token=$token"

    fun getData(): List<JSONObject> {
        return get("$apiUrl$pathNews").jsonArray
            .map { JSONObject(it.toString()) }
    }

}