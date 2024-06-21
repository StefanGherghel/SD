package com.sd.laborator.pojo

import java.time.LocalDateTime

data class CacheData(
    var timestamp: LocalDateTime,
    var searchString: String = "",
    var resultList: List<Person>
)