package com.example.henrymedschallenge.ui.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.henrymedschallenge.model.ProviderSchedule
import com.example.henrymedschallenge.repository.UserSessionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.Instant
import java.time.LocalDate

class MainScreenViewModel : ViewModel() {
    private val userSessionRepository = UserSessionRepository.getInstance()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    private val _providerName = MutableStateFlow<String?>(null)
    val providerName: StateFlow<String?> = _providerName

    private val _providerSchedule = MutableStateFlow<ProviderSchedule?>(null)
    val providerSchedule: StateFlow<ProviderSchedule?> = _providerSchedule

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate

    private val _pendingBookings = MutableStateFlow<List<Instant>>(emptyList())
    val pendingBookings: StateFlow<List<Instant>> = _pendingBookings

    init {
        viewModelScope.launch {
            // TODO move error handling to a common place like a coroutine exception handler
            try {
                userSessionRepository.loadUserAndProvider(1)
            } catch (e: Exception) {
                if (e is IOException || e is CancellationException) {
                    return@launch
                } else {
                    // TODO Call a remote logger service
                    Log.e(this::class.simpleName, "Error loading user and provider", e)
                }
            }
        }

        viewModelScope.launch {
            userSessionRepository.user.collect { user ->
                _userName.value = user?.name
            }
        }

        viewModelScope.launch {
            userSessionRepository.provider.collect { provider ->
                if (provider == null) return@collect

                _providerName.value = provider.name
                _providerSchedule.value = provider.schedule
            }
        }

        viewModelScope.launch {
            userSessionRepository.pendingBookings.collect { bookings ->
                _pendingBookings.value = bookings
            }
        }
    }

    fun selectDate(date: LocalDate?) {
        _selectedDate.value = date
    }

    fun selectTime(selectedTime: Instant) {
        viewModelScope.launch {
            try {
                userSessionRepository.reserveBooking(selectedTime)
            } catch (e: Exception) {
                //repeat same network error handling...
                return@launch
            }

            _selectedDate.value = null
        }
    }

    fun confirmBooking(selectedTime: Instant) {
        viewModelScope.launch {
            try {
                userSessionRepository.confirmBooking(selectedTime)
            } catch (e: Exception) {
                //repeat same network error handling...
                return@launch
            }
        }
    }
}