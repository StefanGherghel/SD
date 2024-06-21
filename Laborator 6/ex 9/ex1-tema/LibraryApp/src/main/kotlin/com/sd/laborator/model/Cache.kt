package com.sd.laborator.model

class Cache(private var timestamp:Int,private var query:String,private var result:String) {
    var cacheTimestamp: Int
        get() {
            return timestamp
        }
        set(value) {
            timestamp = value
        }
    var cacheQuery: String
        get() {
            return query
        }
        set(value) {
            query = value
        }
    var cacheResult: String
        get() {
            return result
        }
        set(value) {
            result = value
        }
}