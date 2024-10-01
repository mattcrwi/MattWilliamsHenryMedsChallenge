package com.example.henrymedschallenge.ui.view

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.henrymedschallenge.model.ProviderSchedule
import com.example.henrymedschallenge.ui.theme.HenryMedsChallengeTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@Composable
fun MainScreen(modifier: Modifier) {
    val viewModel = viewModel<MainScreenViewModel>()
    val name by viewModel.userName.collectAsStateWithLifecycle()
    val providerName by viewModel.providerName.collectAsStateWithLifecycle()
    val providerSchedule by viewModel.providerSchedule.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val pendingBookings by viewModel.pendingBookings.collectAsStateWithLifecycle()

    fun onDaySelect(selectedDate: LocalDate?) {
        viewModel.selectDate(selectedDate)
    }

    fun onTimeSelect(selectedTime: Instant) {
        viewModel.selectTime(selectedTime)
    }

    fun onBookingConfirm(selectedTime: Instant) {
        viewModel.confirmBooking(selectedTime)
    }

    Box(modifier = modifier) {
        MainContent(name, providerName, providerSchedule, selectedDate, pendingBookings, ::onDaySelect, ::onTimeSelect, ::onBookingConfirm)
    }

}

@Composable
fun MainContent(
    name: String?, providerName: String?, providerSchedule: ProviderSchedule?, selectedDate: LocalDate?, pendingBookings: List<Instant>,
    onDaySelect: (LocalDate) -> Unit,
    onTimeSelect: (Instant) -> Unit,
    onBookingConfirm: (Instant) -> Unit
) {
    //This is cheating to change screens because I do not have navigation setup
    if (name == null) {
        Text(text = "Loading...")
    } else if (selectedDate == null) {
        Column {
            Text(text = "Hello $name!")
            Text(text = "Your provider is: $providerName")

            if (pendingBookings.isNotEmpty()) {
                Text(text = "Pending bookings:")
                Button(onClick = { onBookingConfirm(pendingBookings.first()) })
                {
                    Text(
                        text = "Confirm Booking for ${
                            pendingBookings.first().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"))
                        }"
                    )
                }
            }

            Text(text = "Choose a day to book a reservation: ")

            for (date in providerSchedule?.availableDates ?: emptyList()) {
                Button(onClick = { onDaySelect(date) })
                { Text(text = date.toString()) }
            }
        }
    } else {
        Column(modifier = Modifier.scrollable(orientation = Orientation.Vertical, state = rememberScrollState())) {
            Text(text = "Chose a time to reserve for $providerName")
            Text(text = "Selected date: $selectedDate")

            val timeSlots = getTimeSlots(providerSchedule, selectedDate)

            for (timeSlot in timeSlots) {
                Button(onClick = { onTimeSelect(timeSlot.toInstant()) })
                { Text(text = timeSlot.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"))) }
            }
        }
    }
}

//TODO in hindsight, this should be in the view model but I ran out of time. this should have a unit test around it
fun getTimeSlots(providerSchedule: ProviderSchedule?, selectedDate: LocalDate): List<ZonedDateTime> {
    val startDateTime =
        ZonedDateTime.of(selectedDate, providerSchedule?.startTime, providerSchedule?.timeZone?.toZoneId())
            .withZoneSameInstant(ZoneId.systemDefault())

    val endDateTime =
        ZonedDateTime.of(selectedDate, providerSchedule?.endTime, providerSchedule?.timeZone?.toZoneId())
            .withZoneSameInstant(ZoneId.systemDefault())
    val timeSlots = mutableListOf<ZonedDateTime>()
    var currentTime = startDateTime
    while (currentTime.isBefore(endDateTime)) {
        timeSlots.add(currentTime)
        currentTime = currentTime.plusMinutes(15)
    }

    return timeSlots
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400)
@Composable
fun GreetingPreview() {
    HenryMedsChallengeTheme {
        MainContent(
            "Matt", "Provider", ProviderSchedule(
                id = 1,
                availableDates = listOf(LocalDate.now()),
                startTime = LocalTime.of(8, 0),
                endTime = LocalTime.of(17, 0),
                timeZone = TimeZone.getDefault()
            ),
            null,
            listOf(Instant.now()),
            {},
            {},
            {}
        )
    }
}


@Preview(showBackground = true, heightDp = 800, widthDp = 400)
@Composable
fun GreetingPreview2() {
    HenryMedsChallengeTheme {
        MainContent(
            "Matt", "Provider", ProviderSchedule(
                id = 1,
                availableDates = listOf(LocalDate.now()),
                startTime = LocalTime.of(8, 0),
                endTime = LocalTime.of(17, 0),
                timeZone = TimeZone.getDefault()
            ),
            LocalDate.now(),
            listOf(Instant.now()),
            {},
            {},
            {}
        )
    }
}