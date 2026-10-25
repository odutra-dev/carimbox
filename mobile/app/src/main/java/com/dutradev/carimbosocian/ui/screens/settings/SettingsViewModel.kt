package com.dutradev.carimbosocian.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dutradev.carimbosocian.BuildConfig
import com.dutradev.carimbosocian.data.local.PreferencesDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = preferencesDataStore.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val appVersion: String = BuildConfig.VERSION_NAME

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setDarkMode(enabled)
        }
    }
}
