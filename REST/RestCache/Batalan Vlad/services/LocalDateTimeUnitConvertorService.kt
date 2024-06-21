package com.sd.laborator.services

import com.sd.laborator.pojo.DateCustom
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.Month
import java.time.temporal.ChronoUnit

@Service
class LocalDateTimeUnitConvertorService {
    // Second is the bigger one
    fun convertToUnitsDifference(fromDateTime: LocalDateTime, toDateTime: LocalDateTime): DateCustom{

        var tempDateTime = LocalDateTime.from(fromDateTime)

        val years = tempDateTime.until(toDateTime, ChronoUnit.YEARS)
        tempDateTime = tempDateTime.plusYears(years)

        val months = tempDateTime.until(toDateTime, ChronoUnit.MONTHS)
        tempDateTime = tempDateTime.plusMonths(months)

        val days = tempDateTime.until(toDateTime, ChronoUnit.DAYS)
        tempDateTime = tempDateTime.plusDays(days)


        val hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS)
        tempDateTime = tempDateTime.plusHours(hours)

        val minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES)
        tempDateTime = tempDateTime.plusMinutes(minutes)

        val seconds = tempDateTime.until(toDateTime, ChronoUnit.SECONDS)

        return DateCustom(years.toInt(), months.toInt(), days.toInt(), hours.toInt(), minutes.toInt(), seconds.toInt())
    }

}