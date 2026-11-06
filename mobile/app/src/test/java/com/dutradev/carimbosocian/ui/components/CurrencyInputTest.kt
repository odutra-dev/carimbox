package com.dutradev.carimbosocian.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CurrencyInputTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysFormattedCentsToCurrency() {
        composeTestRule.setContent {
            CurrencyInput(
                centsDigits = "5000",
                onCentsDigitsChange = {},
                errorMessage = null
            )
        }

        val node = composeTestRule.onNodeWithText(
            text = "50,00",
            substring = true
        )
        node.assertIsDisplayed()
    }

    @Test
    fun displaysErrorMessageWhenPresent() {
        composeTestRule.setContent {
            CurrencyInput(
                centsDigits = "",
                onCentsDigitsChange = {},
                errorMessage = "Valor é obrigatório"
            )
        }

        composeTestRule.onNodeWithText("Valor é obrigatório").assertIsDisplayed()
    }

    @Test
    fun typingDigitsTriggersOnCentsDigitsChangeWithCleanedDigits() {
        var updatedCents = ""
        composeTestRule.setContent {
            CurrencyInput(
                centsDigits = "100",
                onCentsDigitsChange = { updatedCents = it },
                errorMessage = null
            )
        }

        val inputNode = composeTestRule.onNodeWithText(
            text = "1,00",
            substring = true
        )
        inputNode.performTextInput("5")

        // Valida que caracteres não numéricos são removidos e zeros à esquerda são limpos
        assertEquals("1005", updatedCents)
    }
}
