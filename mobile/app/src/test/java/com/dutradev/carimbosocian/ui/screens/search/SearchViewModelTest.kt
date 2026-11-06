package com.dutradev.carimbosocian.ui.screens.search

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
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<PedidosRepository>()

    @Test
    fun `query change updates uiState query value`() {
        val viewModel = SearchViewModel(repository)
        viewModel.onQueryChange("Advogado")
        assertEquals("Advogado", viewModel.uiState.value.query)
    }

    @Test
    fun `date filter methods set and clear date filter correctly`() {
        val viewModel = SearchViewModel(repository)
        val testDate = Date()

        viewModel.setDateFilter("mes", testDate)
        assertEquals("mes", viewModel.uiState.value.dateFilterType)
        assertEquals(testDate, viewModel.uiState.value.dateFilterValue)

        viewModel.clearDateFilter()
        assertNull(viewModel.uiState.value.dateFilterType)
    }

    @Test
    fun `clearFilters resets query, date filter, results and error`() {
        val viewModel = SearchViewModel(repository)
        viewModel.onQueryChange("Texto")
        viewModel.setDateFilter("ano", Date())

        viewModel.clearFilters()

        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertNull(state.dateFilterType)
        assertTrue(state.pedidos.isEmpty())
        assertFalse(state.searched)
        assertNull(state.errorMessage)
    }

    @Test
    fun `search with pure number up to 7 digits searches by ID and query text`() {
        val pedido = PedidoTestFactory.createPedido(id = 1234L)
        coEvery {
            repository.searchPedidos(
                id = 1234L,
                phoneCandidates = null,
                query = "1234",
                startDate = null,
                endDate = null
            )
        } returns Result.success(listOf(pedido))

        val viewModel = SearchViewModel(repository)
        viewModel.onQueryChange("1234")
        viewModel.search()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.searched)
        assertEquals(1, state.pedidos.size)
        assertEquals(1234L, state.pedidos.first().id)
    }

    @Test
    fun `search with 8 to 9 digits generates local phone and default DDD 13 candidates`() {
        coEvery {
            repository.searchPedidos(
                id = null,
                phoneCandidates = match { candidates ->
                    candidates?.contains(999998888L) == true &&
                            candidates.contains(13999998888L) &&
                            candidates.contains(5513999998888L)
                },
                query = "999998888",
                startDate = null,
                endDate = null
            )
        } returns Result.success(emptyList())

        val viewModel = SearchViewModel(repository)
        viewModel.onQueryChange("999998888")
        viewModel.search()

        assertTrue(viewModel.uiState.value.searched)
        coVerify(exactly = 1) {
            repository.searchPedidos(
                id = null,
                phoneCandidates = any(),
                query = "999998888",
                startDate = null,
                endDate = null
            )
        }
    }

    @Test
    fun `search with 11 digits Brazilian phone generates DDI 55 candidate`() {
        coEvery {
            repository.searchPedidos(
                id = null,
                phoneCandidates = match { candidates ->
                    candidates?.contains(13999998888L) == true &&
                            candidates.contains(5513999998888L)
                },
                query = "13999998888",
                startDate = null,
                endDate = null
            )
        } returns Result.success(emptyList())

        val viewModel = SearchViewModel(repository)
        viewModel.onQueryChange("13999998888")
        viewModel.search()

        coVerify(exactly = 1) {
            repository.searchPedidos(
                id = null,
                phoneCandidates = listOf(13999998888L, 5513999998888L),
                query = "13999998888",
                startDate = null,
                endDate = null
            )
        }
    }

    @Test
    fun `search with 13 digits starting with 55 generates candidate without 55`() {
        coEvery {
            repository.searchPedidos(
                id = null,
                phoneCandidates = match { candidates ->
                    candidates?.contains(5513999998888L) == true &&
                            candidates.contains(13999998888L)
                },
                query = "5513999998888",
                startDate = null,
                endDate = null
            )
        } returns Result.success(emptyList())

        val viewModel = SearchViewModel(repository)
        viewModel.onQueryChange("5513999998888")
        viewModel.search()

        coVerify(exactly = 1) {
            repository.searchPedidos(
                id = null,
                phoneCandidates = listOf(5513999998888L, 13999998888L),
                query = "5513999998888",
                startDate = null,
                endDate = null
            )
        }
    }

    @Test
    fun `search with text query performs text search without id or phone candidates`() {
        val resultList = listOf(PedidoTestFactory.createPedido(cliente = "Gabriel Dutra"))
        coEvery {
            repository.searchPedidos(
                id = null,
                phoneCandidates = null,
                query = "Gabriel Dutra",
                startDate = null,
                endDate = null
            )
        } returns Result.success(resultList)

        val viewModel = SearchViewModel(repository)
        viewModel.onQueryChange("Gabriel Dutra")
        viewModel.search()

        val state = viewModel.uiState.value
        assertEquals(1, state.pedidos.size)
        assertEquals("Gabriel Dutra", state.pedidos.first().cliente)
    }

    @Test
    fun `search handles repository failure and sets errorMessage`() {
        coEvery {
            repository.searchPedidos(any(), any(), any(), any(), any())
        } returns Result.failure(RuntimeException("Falha de conexão com o banco"))

        val viewModel = SearchViewModel(repository)
        viewModel.onQueryChange("qualquer busca")
        viewModel.search()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.searched)
        assertEquals("Falha de conexão com o banco", state.errorMessage)
    }
}
