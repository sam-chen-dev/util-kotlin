package com.example.utlikotlin

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object DateTimeHelper {
    fun createEpochMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val localDate = LocalDate.of(year, month, day)
        val localTime = LocalTime.of(hour, minute)
        val zoneId = ZoneId.systemDefault()

        val zoneDateTime = ZonedDateTime.of(localDate, localTime, zoneId)

        return zoneDateTime.toInstant().toEpochMilli()
    }

    fun calculateDelayToTime(hour: Int, minute: Int): Duration {
        val now = ZonedDateTime.now()

        val todayTargetTime = ZonedDateTime.of(
            LocalDate.now(),
            LocalTime.of(hour, minute),
            ZoneId.systemDefault()
        )

        val nextTargetTime = if (now.isBefore(todayTargetTime)) {
            todayTargetTime
        } else {
            todayTargetTime.plusDays(1)
        }

        return Duration.between(now, nextTargetTime)
    }
}