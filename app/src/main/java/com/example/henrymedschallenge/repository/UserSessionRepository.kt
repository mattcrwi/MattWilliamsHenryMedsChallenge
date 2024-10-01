package com.example.henrymedschallenge.repository

import com.example.henrymedschallenge.model.Provider
import com.example.henrymedschallenge.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant

class UserSessionRepository(networkRepository: NetworkRepositoryInterface) : UserSessionRepositoryInterface {
    companion object {

        @Volatile
        private var instance: UserSessionRepository? = null

        fun getInstance(
            networkRepository: NetworkRepositoryInterface = NetworkRepository.getInstance()
        ) =
            instance ?: synchronized(this) {
                instance ?: UserSessionRepository(networkRepository).also { instance = it }
            }
    }

    private val networkRepository = NetworkRepository.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user
    private val _provider = MutableStateFlow<Provider?>(null)
    var provider: StateFlow<Provider?> = _provider
    private val _pendingBookings = MutableStateFlow<List<Instant>>(emptyList())
    var pendingBookings: StateFlow<List<Instant>> = _pendingBookings

    override suspend fun loadUserAndProvider(userId: Int) {
        val userResponse = networkRepository.serverApi.getUser(userId)
        val providerResponse = networkRepository.serverApi.getProvider(userResponse.providerId)
        _user.value = userResponse
        _provider.value = providerResponse
    }

    override suspend fun reserveBooking(selectedTime: Instant) {
        // The "!!." operator here is not safe and should be replace with null checks
        networkRepository.serverApi.putBookingRequest(_user.value!!.id, _provider.value!!.id, selectedTime)

        _pendingBookings.value = networkRepository.serverApi.getBookingRequests(_user.value!!.id, _provider.value!!.id)
    }

    override suspend fun confirmBooking(selectedTime: Instant) {
        // The "!!." operator here is not safe and should be replace with null checks
        networkRepository.serverApi.confirmBooking(_user.value!!.id, _provider.value!!.id, selectedTime)
        _pendingBookings.value = networkRepository.serverApi.getBookingRequests(_user.value!!.id, _provider.value!!.id)

    }
}

interface UserSessionRepositoryInterface {
    suspend fun loadUserAndProvider(userId: Int)
    suspend fun reserveBooking(selectedTime: Instant)
    suspend fun confirmBooking(selectedTime: Instant)
}