package com.dutradev.carimbosocian.ui.screens.settings

import app.cash.turbine.test
import com.dutradev.carimbosocian.data.local.PreferencesDataStore
import com.dutradev.carimbosocian.data.model.AppLanguage
import com.dutradev.carimbosocian.data.model.ThemeMode
import com.dutradev.carimbosocian.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val preferencesDataStore = mockk<PreferencesDataStore>(relaxed = true)

    @Test
    fun `themeMode emits initial and updated values from preferencesDataStore`() = runTest {
        val themeFlow = MutableStateFlow(ThemeMode.SYSTEM)
        every { preferencesDataStore.themeMode } returns themeFlow
        every { preferencesDataStore.language } returns MutableStateFlow(AppLanguage.SYSTEM)

        val viewModel = SettingsViewModel(preferencesDataStore)

        viewModel.themeMode.test {
            assertEquals(ThemeMode.SYSTEM, awaitItem())

            themeFlow.value = ThemeMode.DARK
            assertEquals(ThemeMode.DARK, awaitItem())

            themeFlow.value = ThemeMode.LIGHT
            assertEquals(ThemeMode.LIGHT, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `language emits initial and updated values from preferencesDataStore`() = runTest {
        val languageFlow = MutableStateFlow(AppLanguage.SYSTEM)
        every { preferencesDataStore.themeMode } returns MutableStateFlow(ThemeMode.SYSTEM)
        every { preferencesDataStore.language } returns languageFlow

        val viewModel = SettingsViewModel(preferencesDataStore)

        viewModel.language.test {
            assertEquals(AppLanguage.SYSTEM, awaitItem())

            languageFlow.value = AppLanguage.PORTUGUESE
            assertEquals(AppLanguage.PORTUGUESE, awaitItem())

            languageFlow.value = AppLanguage.ENGLISH
            assertEquals(AppLanguage.ENGLISH, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setThemeMode calls preferencesDataStore setThemeMode`() = runTest {
        every { preferencesDataStore.themeMode } returns MutableStateFlow(ThemeMode.SYSTEM)
        every { preferencesDataStore.language } returns MutableStateFlow(AppLanguage.SYSTEM)
        coEvery { preferencesDataStore.setThemeMode(any()) } returns Unit

        val viewModel = SettingsViewModel(preferencesDataStore)
        viewModel.setThemeMode(ThemeMode.DARK)

        coVerify(exactly = 1) { preferencesDataStore.setThemeMode(ThemeMode.DARK) }
    }

    @Test
    fun `setLanguage calls preferencesDataStore setLanguage`() = runTest {
        every { preferencesDataStore.themeMode } returns MutableStateFlow(ThemeMode.SYSTEM)
        every { preferencesDataStore.language } returns MutableStateFlow(AppLanguage.SYSTEM)
        coEvery { preferencesDataStore.setLanguage(any()) } returns Unit

        val viewModel = SettingsViewModel(preferencesDataStore)
        viewModel.setLanguage(AppLanguage.SPANISH)

        coVerify(exactly = 1) { preferencesDataStore.setLanguage(AppLanguage.SPANISH) }
    }

    @Test
    fun `appVersion provides version name`() {
        every { preferencesDataStore.themeMode } returns MutableStateFlow(ThemeMode.SYSTEM)
        every { preferencesDataStore.language } returns MutableStateFlow(AppLanguage.SYSTEM)

        val viewModel = SettingsViewModel(preferencesDataStore)
        assertNotNull(viewModel.appVersion)
    }
}
