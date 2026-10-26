package com.dutradev.carimbosocian.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dutradev.carimbosocian.BuildConfig
import com.dutradev.carimbosocian.data.local.PreferencesDataStore
import com.dutradev.carimbosocian.data.model.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = preferencesDataStore.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    val appVersion: String = BuildConfig.VERSION_NAME

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesDataStore.setThemeMode(mode)
        }
    }
}
