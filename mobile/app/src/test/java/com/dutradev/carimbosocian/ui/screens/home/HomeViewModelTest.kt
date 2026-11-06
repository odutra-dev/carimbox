package com.dutradev.carimbosocian.ui.screens.home

import com.dutradev.carimbosocian.data.repository.PedidosRepository
import com.dutradev.carimbosocian.testutil.MainDispatcherRule
import com.dutradev.carimbosocian.testutil.PedidoTestFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<PedidosRepository>()

    @Test
    fun `init loads pedidos with default pending filter successfully`() {
        val pedidos = listOf(
            PedidoTestFactory.createPedido(id = 1L, entregue = false),
            PedidoTestFactory.createPedido(id = 2L, entregue = false)
        )
        coEvery {
            repository.getPedidos(
                statusFilter = "pendente",
                startDate = null,
                endDate = null,
                page = 0,
                pageSize = 20
            )
        } returns Result.success(pedidos)

        val viewModel = HomeViewModel(repository)
        val state = viewModel.uiState.value

        assertEquals(2, state.pedidos.size)
        assertEquals("pendente", state.statusFilter)
        assertFalse(state.isLoading)
        assertFalse(state.hasMore)
        assertNull(state.userMessage)
    }

    @Test
    fun `init handles repository error and updates userMessage`() {
        coEvery {
            repository.getPedidos(any(), any(), any(), any(), any())
        } returns Result.failure(RuntimeException("Falha de conexão"))

        val viewModel = HomeViewModel(repository)
        val state = viewModel.uiState.value

        assertTrue(state.pedidos.isEmpty())
        assertFalse(state.isLoading)
        assertEquals("Falha de conexão", state.userMessage)
    }

    @Test
    fun `setStatusFilter updates filter and reloads pedidos`() {
        coEvery {
            repository.getPedidos(statusFilter = "pendente", any(), any(), any(), any())
        } returns Result.success(emptyList())

        val concluidoPedidos = listOf(
            PedidoTestFactory.createPedido(id = 10L, entregue = true)
        )
        coEvery {
            repository.getPedidos(statusFilter = "concluido", any(), any(), page = 0, any())
        } returns Result.success(concluidoPedidos)

        val viewModel = HomeViewModel(repository)
        viewModel.setStatusFilter("concluido")

        val state = viewModel.uiState.value
        assertEquals("concluido", state.statusFilter)
        assertEquals(1, state.pedidos.size)
        assertEquals(10L, state.pedidos.first().id)
    }

    @Test
    fun `setDateFilter calculates date range and filters list`() {
        coEvery {
            repository.getPedidos(statusFilter = "pendente", any(), any(), any(), any())
        } returns Result.success(emptyList())

        val viewModel = HomeViewModel(repository)
        viewModel.setDateFilter("dia", Date())

        val state = viewModel.uiState.value
        assertEquals("dia", state.dateFilterType)
        coVerify {
            repository.getPedidos(
                statusFilter = "pendente",
                startDate = match { it.contains("T00:00:00") },
                endDate = match { it.contains("T23:59:59") },
                page = 0,
                pageSize = 20
            )
        }
    }

    @Test
    fun `clearDateFilter resets dateFilterType and reloads`() {
        coEvery {
            repository.getPedidos(any(), any(), any(), any(), any())
        } returns Result.success(emptyList())

        val viewModel = HomeViewModel(repository)
        viewModel.setDateFilter("dia", Date())
        viewModel.clearDateFilter()

        val state = viewModel.uiState.value
        assertNull(state.dateFilterType)
    }

    @Test
    fun `loadMore appends new items when hasMore is true`() {
        val initialPage = List(20) { index ->
            PedidoTestFactory.createPedido(id = index.toLong(), entregue = false)
        }
        val secondPage = listOf(
            PedidoTestFactory.createPedido(id = 21L, entregue = false)
        )

        coEvery {
            repository.getPedidos(statusFilter = "pendente", any(), any(), page = 0, pageSize = 20)
        } returns Result.success(initialPage)

        coEvery {
            repository.getPedidos(statusFilter = "pendente", any(), any(), page = 1, pageSize = 20)
        } returns Result.success(secondPage)

        val viewModel = HomeViewModel(repository)
        assertTrue(viewModel.uiState.value.hasMore)

        viewModel.loadMore()

        val state = viewModel.uiState.value
        assertEquals(21, state.pedidos.size)
        assertEquals(1, state.page)
        assertFalse(state.hasMore)
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun `toggleStatus removes item from pending list when marked as completed`() {
        val pedido1 = PedidoTestFactory.createPedido(id = 1L, entregue = false)
        val pedido2 = PedidoTestFactory.createPedido(id = 2L, entregue = false)

        coEvery {
            repository.getPedidos(statusFilter = "pendente", any(), any(), any(), any())
        } returns Result.success(listOf(pedido1, pedido2))

        coEvery {
            repository.toggleStatus(id = 1L, currentStatus = false)
        } returns Result.success(Unit)

        val viewModel = HomeViewModel(repository)
        viewModel.toggleStatus(id = 1L, currentStatus = false)

        val state = viewModel.uiState.value
        assertEquals(1, state.pedidos.size)
        assertEquals(2L, state.pedidos.first().id)
    }

    @Test
    fun `clearUserMessage resets error message to null`() {
        coEvery {
            repository.getPedidos(any(), any(), any(), any(), any())
        } returns Result.failure(RuntimeException("Erro temporário"))

        val viewModel = HomeViewModel(repository)
        assertEquals("Erro temporário", viewModel.uiState.value.userMessage)

        viewModel.clearUserMessage()
        assertNull(viewModel.uiState.value.userMessage)
    }
}
