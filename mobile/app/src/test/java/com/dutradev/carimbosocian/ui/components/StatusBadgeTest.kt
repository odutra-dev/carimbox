package com.dutradev.carimbosocian.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.dutradev.carimbosocian.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
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

        val expected = RuntimeEnvironment.getApplication().getString(R.string.concluidos)
        composeTestRule.onNodeWithText(expected).assertIsDisplayed()
    }

    @Test
    fun displaysPendentesWhenDeliveredIsFalse() {
        composeTestRule.setContent {
            StatusBadge(isDelivered = false)
        }

        val expected = RuntimeEnvironment.getApplication().getString(R.string.pendentes)
        composeTestRule.onNodeWithText(expected).assertIsDisplayed()
    }
}
