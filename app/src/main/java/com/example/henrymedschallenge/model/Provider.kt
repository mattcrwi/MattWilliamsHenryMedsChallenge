package com.example.henrymedschallenge.model

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.TimeZone

data class Provider(val id: Int, val name: String, var schedule: ProviderSchedule)

/**
 *  This class represents a provider's schedule.
 *  Availability would need to be more granular than just a list of dates in a production app.
 */
data class ProviderSchedule(
    val id: Int,
    var availableDates: List<LocalDate>,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val timeZone: TimeZone,
) {
    var providerTimeZone = timeZone
    private var bookings: MutableList<Instant> = mutableListOf()
}