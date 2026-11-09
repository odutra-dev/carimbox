package com.dutradev.carimbosocian.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class StatusBadgeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysConcluidosWhenDeliveredIsTrue() {
        composeTestRule.setContent {
            StatusBadge(isDelivered = true)
        }

        composeTestRule.onNodeWithText("Concluídos").assertIsDisplayed()
    }

    @Test
    fun displaysPendentesWhenDeliveredIsFalse() {
        composeTestRule.setContent {
            StatusBadge(isDelivered = false)
        }

        composeTestRule.onNodeWithText("Pendentes").assertIsDisplayed()
    }
}
