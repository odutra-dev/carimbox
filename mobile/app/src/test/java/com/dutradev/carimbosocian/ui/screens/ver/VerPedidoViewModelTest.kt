package com.dutradev.carimbosocian.ui.screens.ver

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
class VerPedidoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<PedidosRepository>()

    @Test
    fun `init loads pedido by id successfully`() {
        val pedido = PedidoTestFactory.createPedido(id = 55L, cliente = "Lucas Mendes")
        coEvery { repository.getPedidoById(55L) } returns Result.success(pedido)

        val viewModel = VerPedidoViewModel(55L, repository)
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNotNull(state.pedido)
        assertEquals("Lucas Mendes", state.pedido?.cliente)
        assertNull(state.errorMessage)
    }

    @Test
    fun `init handles error when pedido is not found`() {
        coEvery { repository.getPedidoById(999L) } returns Result.failure(RuntimeException("Pedido não encontrado"))

        val viewModel = VerPedidoViewModel(999L, repository)
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertNull(state.pedido)
        assertEquals("Pedido não encontrado", state.errorMessage)
    }

    @Test
    fun `toggleStatus updates status optimistically and inverts entregue state`() {
        val pedido = PedidoTestFactory.createPedido(id = 55L, entregue = false)
        coEvery { repository.getPedidoById(55L) } returns Result.success(pedido)
        coEvery { repository.toggleStatus(55L, false) } returns Result.success(Unit)

        val viewModel = VerPedidoViewModel(55L, repository)
        assertFalse(viewModel.uiState.value.pedido!!.entregue)

        viewModel.toggleStatus()

        val state = viewModel.uiState.value
        assertTrue(state.pedido!!.entregue)
        assertNull(state.errorMessage)
        coVerify(exactly = 1) { repository.toggleStatus(55L, false) }
    }

    @Test
    fun `toggleStatus sets error message when repository fails`() {
        val pedido = PedidoTestFactory.createPedido(id = 55L, entregue = false)
        coEvery { repository.getPedidoById(55L) } returns Result.success(pedido)
        coEvery { repository.toggleStatus(55L, false) } returns Result.failure(RuntimeException("Erro de rede"))

        val viewModel = VerPedidoViewModel(55L, repository)
        viewModel.toggleStatus()

        val state = viewModel.uiState.value
        assertFalse(state.pedido!!.entregue)
        assertTrue(state.errorMessage!!.contains("Erro de rede"))
    }

    @Test
    fun `deletePedido executes repository deletion and invokes callback`() {
        val pedido = PedidoTestFactory.createPedido(id = 55L)
        coEvery { repository.getPedidoById(55L) } returns Result.success(pedido)
        coEvery { repository.deletePedido(55L) } returns Result.success(Unit)

        val viewModel = VerPedidoViewModel(55L, repository)
        var deletedCallbackCalled = false

        viewModel.deletePedido {
            deletedCallbackCalled = true
        }

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isDeleted)
        assertTrue(deletedCallbackCalled)
        coVerify(exactly = 1) { repository.deletePedido(55L) }
    }

    @Test
    fun `deletePedido handles error and does not invoke callback`() {
        val pedido = PedidoTestFactory.createPedido(id = 55L)
        coEvery { repository.getPedidoById(55L) } returns Result.success(pedido)
        coEvery { repository.deletePedido(55L) } returns Result.failure(RuntimeException("Falha de permissão"))

        val viewModel = VerPedidoViewModel(55L, repository)
        var deletedCallbackCalled = false

        viewModel.deletePedido {
            deletedCallbackCalled = true
        }

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isDeleted)
        assertFalse(deletedCallbackCalled)
        assertTrue(state.errorMessage!!.contains("Falha de permissão"))
    }

    @Test
    fun `clearErrorMessage resets errorMessage to null`() {
        coEvery { repository.getPedidoById(1L) } returns Result.failure(RuntimeException("Erro temporário"))

        val viewModel = VerPedidoViewModel(1L, repository)
        assertNotNull(viewModel.uiState.value.errorMessage)

        viewModel.clearErrorMessage()
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
