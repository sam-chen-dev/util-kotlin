package com.example.utlikotlin

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object DateTimeHelper {
    fun nowInSystemMillis() = LocalDateTime.now().toSystemMillis()

    fun nowInFormattedString(format: String) = nowInSystemMillis().toFormattedDateTimeString(format)

    fun todayStartInSystemMillis() = LocalDateTime.now().with(LocalTime.MIN).toSystemMillis()

    fun todayEndInSystemMillis() = LocalDateTime.now().with(LocalTime.MAX).toSystemMillis()

    fun futureDayStartInSystemMillis(dayCount: Long) = LocalDateTime.now().plusDays(dayCount).with(LocalTime.MIN).toSystemMillis()

    fun futureDayEndInSystemMillis(dayCount: Long) = LocalDateTime.now().plusDays(dayCount).with(LocalTime.MAX).toSystemMillis()

    fun pastDayStartInSystemMillis(dayCount: Long) = LocalDateTime.now().minusDays(dayCount).with(LocalTime.MIN).toSystemMillis()

    fun pastDayEndInSystemMillis(dayCount: Long) = LocalDateTime.now().minusDays(dayCount).with(LocalTime.MAX).toSystemMillis()

    fun createEpochMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val localDate = LocalDate.of(year, month, day)
        val localTime = LocalTime.of(hour, minute)
        val zoneId = ZoneId.systemDefault()

        val zoneDateTime = ZonedDateTime.of(localDate, localTime, zoneId)

        return zoneDateTime.toInstant().toEpochMilli()
    }
}