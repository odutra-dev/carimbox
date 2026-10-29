package com.dutradev.carimbosocian

import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.os.ConfigurationCompat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.dutradev.carimbosocian.data.model.AppLanguage
import com.dutradev.carimbosocian.data.model.ThemeMode
import com.dutradev.carimbosocian.ui.navigation.CarimboxNavGraph
import com.dutradev.carimbosocian.ui.theme.CarimboxTheme
import java.util.Locale

val LocalActivity = staticCompositionLocalOf<ComponentActivity?> { null }

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

            val appLanguage by appContainer.preferencesDataStore.language.collectAsState(initial = AppLanguage.SYSTEM)

            val currentLocale = remember(appLanguage) {
                when (appLanguage) {
                    AppLanguage.SYSTEM -> {
                        val systemLocales = ConfigurationCompat.getLocales(Resources.getSystem().configuration)
                        if (!systemLocales.isEmpty) systemLocales[0] ?: Locale.getDefault() else Locale.getDefault()
                    }
                    AppLanguage.PORTUGUESE -> Locale("pt", "BR")
                    AppLanguage.ENGLISH -> Locale.US
                    AppLanguage.SPANISH -> Locale("es")
                }
            }

            val configuration = LocalConfiguration.current
            val localizedConfig = remember(configuration, currentLocale) {
                Configuration(configuration).apply {
                    setLocale(currentLocale)
                    setLayoutDirection(currentLocale)
                }
            }
            val baseContext = LocalContext.current
            val localizedContext = remember(baseContext, currentLocale) {
                val config = Configuration(baseContext.resources.configuration)
                config.setLocale(currentLocale)
                config.setLayoutDirection(currentLocale)
                val configContext = baseContext.createConfigurationContext(config)
                object : ContextWrapper(baseContext) {
                    override fun getResources(): Resources = configContext.resources
                    override fun getAssets(): AssetManager = configContext.assets
                }
            }

            DisposableEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { isDark },
                    navigationBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { isDark }
                )
                onDispose {}
            }

            CompositionLocalProvider(
                LocalActivity provides this@MainActivity,
                LocalConfiguration provides localizedConfig,
                LocalContext provides localizedContext
            ) {
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
}
