package com.example.henrymedschallenge

import com.example.henrymedschallenge.model.Provider
import com.example.henrymedschallenge.model.ProviderSchedule
import com.example.henrymedschallenge.model.User
import com.example.henrymedschallenge.network.ServerApiInterface
import com.example.henrymedschallenge.repository.NetworkRepositoryInterface
import com.example.henrymedschallenge.repository.UserSessionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalTime
import java.time.ZoneId
import java.util.TimeZone

class UserSessionRepositoryTest {
    private var userSessionRepository: UserSessionRepository? = null
    private val networkRepository: NetworkRepositoryInterface = mock()
    private val serverApi: ServerApiInterface = mock()

    @Before
    fun setup() {
        userSessionRepository = UserSessionRepository.getInstance(networkRepository = networkRepository)
    }

    @Test
    fun setUser() {
        val userId = 1
        val user = User(1, "Matt", 1)
        val provider = Provider(
            1,
            "Dr. Howard",
            ProviderSchedule(1, listOf(), LocalTime.of(8, 0), LocalTime.of(15, 0), TimeZone.getTimeZone(ZoneId.of("America/New_York")))
        )
        runTest {
            whenever(networkRepository.serverApi).thenReturn(serverApi)
            whenever(networkRepository.serverApi.getUser(1)).thenReturn(user)
            whenever(networkRepository.serverApi.getProvider(1)).thenReturn(provider)
            userSessionRepository?.loadUserAndProvider(userId)
        }

        assertEquals(userId, userSessionRepository?.user?.value?.id)
    }

    //TODO add tests for other functions. An important one to test would be
}