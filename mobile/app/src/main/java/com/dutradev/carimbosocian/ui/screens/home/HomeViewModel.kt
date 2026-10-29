package com.dutradev.carimbosocian.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dutradev.carimbosocian.data.model.Pedido
import com.dutradev.carimbosocian.data.repository.PedidosRepository
import com.dutradev.carimbosocian.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class HomeUiState(
    val pedidos: List<Pedido> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val statusFilter: String? = "pendente", // "pendente", "concluido", null (todos)
    val dateFilterType: String? = null,    // "dia", "mes", "ano", null
    val dateFilterValue: Date = Date(),
    val page: Int = 0,
    val hasMore: Boolean = true,
    val userMessage: String? = null
)

class HomeViewModel(
    private val repository: PedidosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val pageSize = 20

    init {
        loadPedidos(isInitial = true)
    }

    fun setStatusFilter(filter: String?) {
        if (_uiState.value.statusFilter == filter) return
        _uiState.update { it.copy(statusFilter = filter, page = 0, hasMore = true) }
        loadPedidos(isInitial = true)
    }

    fun setDateFilter(type: String?, date: Date) {
        _uiState.update { it.copy(dateFilterType = type, dateFilterValue = date, page = 0, hasMore = true) }
        loadPedidos(isInitial = true)
    }

    fun clearDateFilter() {
        _uiState.update { it.copy(dateFilterType = null, page = 0, hasMore = true) }
        loadPedidos(isInitial = true)
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true, page = 0, hasMore = true) }
        loadPedidos(isInitial = true, isRefresh = true)
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.hasMore) return

        _uiState.update { it.copy(isLoadingMore = true) }
        val nextPage = state.page + 1

        viewModelScope.launch {
            val range = getDateRange(state.dateFilterType, state.dateFilterValue)
            val result = repository.getPedidos(
                statusFilter = state.statusFilter,
                startDate = range?.first,
                endDate = range?.second,
                page = nextPage,
                pageSize = pageSize
            )

            result.onSuccess { newItems ->
                _uiState.update {
                    it.copy(
                        pedidos = (it.pedidos + newItems).distinctBy { p -> p.id },
                        isLoadingMore = false,
                        page = nextPage,
                        hasMore = newItems.size >= pageSize
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoadingMore = false) }
            }
        }
    }

    private fun loadPedidos(isInitial: Boolean = false, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isInitial && !isRefresh) {
                _uiState.update { it.copy(isLoading = true, userMessage = null) }
            }

            val state = _uiState.value
            val range = getDateRange(state.dateFilterType, state.dateFilterValue)

            val result = repository.getPedidos(
                statusFilter = state.statusFilter,
                startDate = range?.first,
                endDate = range?.second,
                page = 0,
                pageSize = pageSize
            )

            result.onSuccess { items ->
                _uiState.update {
                    it.copy(
                        pedidos = items.distinctBy { p -> p.id },
                        isLoading = false,
                        isRefreshing = false,
                        page = 0,
                        hasMore = items.size >= pageSize
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        userMessage = err.localizedMessage ?: "Erro ao carregar pedidos"
                    )
                }
            }
        }
    }

    fun toggleStatus(id: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            val result = repository.toggleStatus(id, currentStatus)
            result.onSuccess {
                // Se estiver filtrando por pendente/concluido, remove o item ou atualiza
                _uiState.update { state ->
                    val updatedList = state.pedidos.mapNotNull { p ->
                        if (p.id == id) {
                            val newP = p.copy(entregue = !currentStatus)
                            if (state.statusFilter == "pendente" && newP.entregue) null
                            else if (state.statusFilter == "concluido" && !newP.entregue) null
                            else newP
                        } else p
                    }
                    state.copy(pedidos = updatedList)
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(userMessage = "Erro ao atualizar status: ${err.localizedMessage}")
                }
            }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun getDateRange(type: String?, date: Date): Pair<String, String>? {
        return when (type) {
            "dia" -> DateUtils.getDayRange(date)
            "mes" -> DateUtils.getMonthRange(date)
            "ano" -> DateUtils.getYearRange(date)
            else -> null
        }
    }
}
