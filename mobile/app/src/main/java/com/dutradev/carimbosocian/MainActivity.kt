package com.dutradev.carimbosocian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.dutradev.carimbosocian.data.model.ThemeMode
import com.dutradev.carimbosocian.ui.navigation.CarimboxNavGraph
import com.dutradev.carimbosocian.ui.theme.CarimboxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as CarimboxApplication).container

        setContent {
            val systemDark = isSystemInDarkTheme()
            val themeMode by appContainer.preferencesDataStore.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val isDark = when (themeMode) {
                ThemeMode.SYSTEM -> systemDark
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            CarimboxTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                CarimboxNavGraph(
                    navController = navController,
                    appContainer = appContainer
                )
            }
        }
    }
}
