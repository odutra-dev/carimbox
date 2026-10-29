package com.dutradev.carimbosocian.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dutradev.carimbosocian.data.model.AppLanguage
import com.dutradev.carimbosocian.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "carimbox_settings")

class PreferencesDataStore(private val context: Context) {

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_LANGUAGE = stringPreferencesKey("app_language")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val raw = preferences[KEY_THEME_MODE]
            try {
                if (raw != null) ThemeMode.valueOf(raw) else ThemeMode.SYSTEM
            } catch (_: Exception) {
                ThemeMode.SYSTEM
            }
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode.name
        }
    }

    val language: Flow<AppLanguage> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val raw = preferences[KEY_LANGUAGE]
            AppLanguage.fromCode(raw)
        }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = language.code
        }
    }
}
