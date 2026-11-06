package com.dutradev.carimbosocian.ui.screens.atualizar

import com.dutradev.carimbosocian.R
import com.dutradev.carimbosocian.data.model.PedidoUpdate
import com.dutradev.carimbosocian.data.repository.PedidosRepository
import com.dutradev.carimbosocian.testutil.MainDispatcherRule
import com.dutradev.carimbosocian.testutil.PedidoTestFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AtualizarPedidoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<PedidosRepository>(relaxed = true)

    @Test
    fun `init loads existing order data and sets state correctly`() {
        val pedido = PedidoTestFactory.createPedido(
            id = 42L,
            cliente = "Silvia Helena",
            whatsapp = 5513999998888L,
            tipoModelo = "Nykon 302",
            textoLogo = "Silvia Dentista CRO 123",
            tipoPagamento = "Cartão de Débito",
            valor = 50.0,
            observacao = "Entrega urgente",
            entregue = true,
            dataEntrega = "2026-11-20T10:00:00Z",
            label = "Personalizado"
        )

        coEvery { repository.getPedidoById(42L) } returns Result.success(pedido)

        val viewModel = AtualizarPedidoViewModel(42L, repository)
        val state = viewModel.uiState.value

        assertFalse(state.isLoadingData)
        assertEquals("Silvia Helena", state.cliente)
        assertEquals("55", state.selectedDdi)
        assertEquals("13999998888", state.whatsapp)
        assertEquals("Nykon 302", state.tipoModelo)
        assertEquals("Silvia Dentista CRO 123", state.textoLogo)
        assertEquals("Cartão de Débito", state.tipoPagamento)
        assertEquals("5000", state.valor) // 50.0 formatado em centavos
        assertEquals("Entrega urgente", state.observacao)
        assertTrue(state.entregue)
        assertEquals("Personalizado", state.label)
        assertNull(state.errorMessage)
    }

    @Test
    fun `init handles getPedidoById error and sets errorMessage`() {
        coEvery { repository.getPedidoById(99L) } returns Result.failure(RuntimeException("Pedido não encontrado"))

        val viewModel = AtualizarPedidoViewModel(99L, repository)
        val state = viewModel.uiState.value

        assertFalse(state.isLoadingData)
        assertEquals("Pedido não encontrado", state.errorMessage)
    }

    @Test
    fun `submit fails with validation error when required field is empty`() {
        val pedido = PedidoTestFactory.createPedido(id = 1L)
        coEvery { repository.getPedidoById(1L) } returns Result.success(pedido)

        val viewModel = AtualizarPedidoViewModel(1L, repository)
        var callbackCalled = false

        viewModel.onTextoLogoChange("") // Campo obrigatório esvaziado
        viewModel.submit { callbackCalled = true }

        val state = viewModel.uiState.value
        assertEquals(R.string.erro_texto_logo_obrigatorio, state.textoLogoErrorResId)
        assertEquals(R.string.alerta_campos_obrigatorios, state.errorMessageResId)
        assertFalse(callbackCalled)
        coVerify(exactly = 0) { repository.updatePedido(any(), any()) }
    }

    @Test
    fun `submit updates order successfully and triggers callback`() {
        val pedido = PedidoTestFactory.createPedido(id = 1L, whatsapp = 13999998888L, valor = 30.0)
        coEvery { repository.getPedidoById(1L) } returns Result.success(pedido)
        coEvery { repository.updatePedido(1L, any()) } returns Result.success(Unit)

        val viewModel = AtualizarPedidoViewModel(1L, repository)
        var callbackCalled = false

        viewModel.onClienteChange("Novo Nome Cliente")
        viewModel.onValorChange("4500") // R$ 45,00
        viewModel.submit { callbackCalled = true }

        val state = viewModel.uiState.value
        assertFalse(state.isSaving)
        assertTrue(state.isSuccess)
        assertTrue(callbackCalled)

        coVerify(exactly = 1) {
            repository.updatePedido(
                eq(1L),
                match { update: PedidoUpdate ->
                    update.cliente == "Novo Nome Cliente" &&
                            update.valor == 45.0
                }
            )
        }
    }

    @Test
    fun `submit handles repository update failure`() {
        val pedido = PedidoTestFactory.createPedido(id = 1L)
        coEvery { repository.getPedidoById(1L) } returns Result.success(pedido)
        coEvery { repository.updatePedido(1L, any()) } returns Result.failure(RuntimeException("Erro no update"))

        val viewModel = AtualizarPedidoViewModel(1L, repository)
        var callbackCalled = false

        viewModel.submit { callbackCalled = true }

        val state = viewModel.uiState.value
        assertFalse(state.isSaving)
        assertFalse(state.isSuccess)
        assertFalse(callbackCalled)
        assertEquals("Erro no update", state.errorMessage)
    }

    @Test
    fun `clearErrorMessage resets all error flags`() {
        val pedido = PedidoTestFactory.createPedido(id = 1L)
        coEvery { repository.getPedidoById(1L) } returns Result.success(pedido)

        val viewModel = AtualizarPedidoViewModel(1L, repository)
        viewModel.onTextoLogoChange("")
        viewModel.submit {}

        assertNotNull(viewModel.uiState.value.errorMessageResId)

        viewModel.clearErrorMessage()

        val state = viewModel.uiState.value
        assertNull(state.errorMessageResId)
        assertNull(state.errorMessage)
    }
}
