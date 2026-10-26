package com.dutradev.carimbosocian.ui.screens.search

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

data class SearchUiState(
    val query: String = "",
    val dateFilterType: String? = null,
    val dateFilterValue: Date = Date(),
    val pedidos: List<Pedido> = emptyList(),
    val isLoading: Boolean = false,
    val searched: Boolean = false,
    val errorMessage: String? = null
)

class SearchViewModel(
    private val repository: PedidosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChange(value: String) = _uiState.update { it.copy(query = value) }

    fun setDateFilter(type: String?, date: Date) {
        _uiState.update { it.copy(dateFilterType = type, dateFilterValue = date) }
    }

    fun clearDateFilter() {
        _uiState.update { it.copy(dateFilterType = null) }
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                query = "",
                dateFilterType = null,
                dateFilterValue = Date(),
                pedidos = emptyList(),
                searched = false,
                errorMessage = null
            )
        }
    }

    fun search() {
        val state = _uiState.value
        val trimmed = state.query.trim()
        val id = trimmed.toLongOrNull()
        val textQuery = if (id == null) trimmed.ifBlank { null } else null

        val range = when (state.dateFilterType) {
            "dia" -> DateUtils.getDayRange(state.dateFilterValue)
            "mes" -> DateUtils.getMonthRange(state.dateFilterValue)
            "ano" -> DateUtils.getYearRange(state.dateFilterValue)
            else -> null
        }

        _uiState.update { it.copy(isLoading = true, searched = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.searchPedidos(
                id = id,
                query = textQuery,
                startDate = range?.first,
                endDate = range?.second
            )

            result.onSuccess { list ->
                _uiState.update { it.copy(pedidos = list, isLoading = false) }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Erro na busca"
                    )
                }
            }
        }
    }
}
