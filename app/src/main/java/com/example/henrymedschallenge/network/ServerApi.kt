package com.example.henrymedschallenge.network

import com.example.henrymedschallenge.model.Provider
import com.example.henrymedschallenge.model.ProviderSchedule
import com.example.henrymedschallenge.model.User
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.TimeZone

/**
 *  This class would be normally be an interface to an API using Retrofit or similar.
 */
class ServerApi : ServerApiInterface {
    override suspend fun getProvider(providerId: Int): Provider {
        return Provider(
            providerId, "Dr. Howard", ProviderSchedule(
                providerId,
                listOf(LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), LocalDate.now().plusDays(3)),
                LocalTime.of(8, 0),
                LocalTime.of(15, 0),
                TimeZone.getTimeZone(ZoneId.of("America/New_York"))
            )
        )
    }

    //This is to cheat to pretend the server changes state
    private var cheatingBookingRequest: Instant? = null

    override suspend fun getUser(userId: Int): User {
        return User(userId, "Matt", providerId = 1)
    }

    override suspend fun putBookingRequest(userId: Int, providerId: Int, dateTime: Instant) {
        cheatingBookingRequest = dateTime
    }

    override suspend fun confirmBooking(userId: Int, providerId: Int, dateTime: Instant) {
        cheatingBookingRequest = null
    }

    override suspend fun getBookingRequests(userId: Int, providerId: Int): List<Instant> {
        return if (cheatingBookingRequest != null) listOf(cheatingBookingRequest!!) else emptyList()
    }
}

interface ServerApiInterface {
    suspend fun getProvider(providerId: Int): Provider
    suspend fun getUser(userId: Int): User
    suspend fun putBookingRequest(userId: Int, providerId: Int, dateTime: Instant)
    suspend fun confirmBooking(userId: Int, providerId: Int, dateTime: Instant)
    suspend fun getBookingRequests(userId: Int, providerId: Int): List<Instant>

}