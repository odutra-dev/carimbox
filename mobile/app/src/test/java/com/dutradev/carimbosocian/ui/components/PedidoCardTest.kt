package com.dutradev.carimbosocian.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dutradev.carimbosocian.R
import com.dutradev.carimbosocian.testutil.PedidoTestFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PedidoCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysPedidoInformationCorrectly() {
        val pedido = PedidoTestFactory.createPedido(
            id = 777L,
            cliente = "Fernanda Lima",
            whatsapp = 5513999998888L,
            dataPedido = "2026-11-06T10:00:00Z",
            entregue = false
        )

        composeTestRule.setContent {
            PedidoCard(
                pedido = pedido,
                onClick = {},
                onToggleStatus = { _, _ -> }
            )
        }

        val context = RuntimeEnvironment.getApplication()
        val btnConcluidoDesc = context.getString(R.string.marcar_como_concluido_titulo)
        val btnWhatsappDesc = context.getString(R.string.abrir_whatsapp)

        composeTestRule.onNodeWithText("#777", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Fernanda Lima", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(btnConcluidoDesc).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(btnWhatsappDesc).assertIsDisplayed()
    }

    @Test
    fun triggersOnClickWhenCardIsClicked() {
        val pedido = PedidoTestFactory.createPedido(id = 12L)
        var clicked = false

        composeTestRule.setContent {
            PedidoCard(
                pedido = pedido,
                onClick = { clicked = true },
                onToggleStatus = { _, _ -> }
            )
        }

        composeTestRule.onNodeWithText("#12", substring = true).performClick()
        assertTrue(clicked)
    }

    @Test
    fun doesNotShowCompleteButtonWhenAlreadyDelivered() {
        val pedido = PedidoTestFactory.createPedido(id = 88L, entregue = true)

        composeTestRule.setContent {
            PedidoCard(
                pedido = pedido,
                onClick = {},
                onToggleStatus = { _, _ -> }
            )
        }

        val context = RuntimeEnvironment.getApplication()
        val btnConcluidoDesc = context.getString(R.string.marcar_como_concluido_titulo)
        val btnWhatsappDesc = context.getString(R.string.abrir_whatsapp)

        composeTestRule.onNodeWithContentDescription(btnConcluidoDesc).assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription(btnWhatsappDesc).assertIsDisplayed()
    }

    @Test
    fun opensConfirmationDialogAndCallsOnToggleStatus() {
        val pedido = PedidoTestFactory.createPedido(id = 50L, entregue = false)
        var toggledId: Long? = null
        var toggledStatus: Boolean? = null

        composeTestRule.setContent {
            PedidoCard(
                pedido = pedido,
                onClick = {},
                onToggleStatus = { id, current ->
                    toggledId = id
                    toggledStatus = current
                }
            )
        }

        val context = RuntimeEnvironment.getApplication()
        val btnConcluidoDesc = context.getString(R.string.marcar_como_concluido_titulo)
        val btnConfirmar = context.getString(R.string.confirmar)

        // Clica no botão de concluir
        composeTestRule.onNodeWithContentDescription(btnConcluidoDesc).performClick()

        // Diálogo deve abrir com botão Confirmar
        composeTestRule.onNodeWithText(btnConfirmar).assertIsDisplayed()
        composeTestRule.onNodeWithText(btnConfirmar).performClick()

        assertEquals(50L, toggledId)
        assertEquals(false, toggledStatus)
    }
}
