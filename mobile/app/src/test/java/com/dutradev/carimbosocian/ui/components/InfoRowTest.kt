package com.dutradev.carimbosocian.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class InfoRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysLabelAndValueCorrectly() {
        composeTestRule.setContent {
            InfoRow(
                label = "Cliente",
                value = "Roberto Santos",
                icon = Icons.Default.Person
            )
        }

        composeTestRule.onNodeWithText("Cliente").assertIsDisplayed()
        composeTestRule.onNodeWithText("Roberto Santos").assertIsDisplayed()
    }

    @Test
    fun displaysDashFallbackWhenValueIsBlank() {
        composeTestRule.setContent {
            InfoRow(
                label = "Observação",
                value = ""
            )
        }

        composeTestRule.onNodeWithText("Observação").assertIsDisplayed()
        composeTestRule.onNodeWithText("-").assertIsDisplayed()
    }

    @Test
    fun showsCopyButtonWhenEnabledAndValueNotEmpty() {
        composeTestRule.setContent {
            InfoRow(
                label = "Telefone",
                value = "(13) 99999-8888",
                enableCopy = true
            )
        }

        composeTestRule.onNodeWithContentDescription("Copiar Telefone").assertIsDisplayed()
    }

    @Test
    fun doesNotShowCopyButtonWhenEnableCopyIsFalse() {
        composeTestRule.setContent {
            InfoRow(
                label = "Telefone",
                value = "(13) 99999-8888",
                enableCopy = false
            )
        }

        composeTestRule.onNodeWithContentDescription("Copiar Telefone").assertDoesNotExist()
    }

    @Test
    fun triggersOnClickCallbackWhenRowIsClicked() {
        var clicked = false
        composeTestRule.setContent {
            InfoRow(
                label = "Detalhes",
                value = "Ver mais",
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Detalhes").performClick()
        assertTrue(clicked)
    }
}
