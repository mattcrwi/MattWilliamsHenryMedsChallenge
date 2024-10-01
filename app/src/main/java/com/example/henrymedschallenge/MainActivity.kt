package com.example.henrymedschallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.henrymedschallenge.repository.NetworkRepository
import com.example.henrymedschallenge.repository.UserSessionRepository
import com.example.henrymedschallenge.ui.theme.HenryMedsChallengeTheme
import com.example.henrymedschallenge.ui.view.MainScreen

class MainActivity : ComponentActivity() {
    private var providerScheduleRepository: UserSessionRepository? = null
    private var networkRepository: NetworkRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HenryMedsChallengeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // TODO implement Navigation
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        providerScheduleRepository = UserSessionRepository.getInstance()
        networkRepository = NetworkRepository.getInstance()
    }
}

