package com.dutradev.carimbosocian.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PhoneWithDdiInputTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysSelectedDdiAndMaskedPhone() {
        composeTestRule.setContent {
            PhoneWithDdiInput(
                selectedDdi = "55",
                phoneDigits = "13999998888",
                onDdiChange = {},
                onPhoneDigitsChange = {},
                errorMessage = null
            )
        }

        composeTestRule.onNodeWithText("+55").assertIsDisplayed()
        composeTestRule.onNodeWithText("🇧🇷").assertIsDisplayed()
        composeTestRule.onNodeWithText("(13) 99999-8888").assertIsDisplayed()
    }

    @Test
    fun displaysErrorMessageWhenPresent() {
        composeTestRule.setContent {
            PhoneWithDdiInput(
                selectedDdi = "55",
                phoneDigits = "",
                onDdiChange = {},
                onPhoneDigitsChange = {},
                errorMessage = "WhatsApp obrigatório"
            )
        }

        composeTestRule.onNodeWithText("WhatsApp obrigatório").assertIsDisplayed()
    }

    @Test
    fun typingDigitsTriggersOnPhoneDigitsChange() {
        var typedDigits = ""
        composeTestRule.setContent {
            PhoneWithDdiInput(
                selectedDdi = "55",
                phoneDigits = "1399",
                onDdiChange = {},
                onPhoneDigitsChange = { typedDigits = it },
                errorMessage = null
            )
        }

        val inputNode = composeTestRule.onNodeWithText("(13) 99", substring = true)
        inputNode.performTextReplacement("13999")

        assertEquals("13999", typedDigits)
    }

    @Test
    fun clickingDdiOpensMenuAndSelectingCountryChangesDdi() {
        var selectedDdi = "55"
        composeTestRule.setContent {
            PhoneWithDdiInput(
                selectedDdi = selectedDdi,
                phoneDigits = "",
                onDdiChange = { selectedDdi = it },
                onPhoneDigitsChange = {},
                errorMessage = null
            )
        }

        // Clica no seletor DDI
        composeTestRule.onNodeWithText("+55").performClick()

        // Seleciona Estados Unidos (+1)
        composeTestRule.onNodeWithText("+1", substring = true).performClick()

        assertEquals("1", selectedDdi)
    }
}
