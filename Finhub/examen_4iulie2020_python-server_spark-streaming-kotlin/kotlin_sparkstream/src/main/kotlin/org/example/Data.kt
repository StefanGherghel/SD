package org.example

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    var name: String,
    var phone: String,
    var ipo: String = "2020-1-1",
    var marketCapitalization: Double,
    var exchange: String
)