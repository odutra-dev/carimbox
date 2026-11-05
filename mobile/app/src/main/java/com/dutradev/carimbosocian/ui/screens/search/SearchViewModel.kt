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
        val digits = trimmed.filter { it.isDigit() }
        val hasLetters = trimmed.any { it.isLetter() }

        val id: Long?
        val phoneCandidates = mutableListOf<Long>()
        val textQuery: String? = trimmed.ifBlank { null }

        when {
            // Se for número puro com até 7 dígitos, busca por ID (e também no texto)
            digits.isNotEmpty() && digits == trimmed && digits.length <= 7 -> {
                id = digits.toLongOrNull()
            }
            // Se NÃO tem letras e tem entre 8 e 15 dígitos numéricos, é reconhecido como telefone
            !hasLetters && digits.length in 8..15 -> {
                id = null
                val num = digits.toLongOrNull()
                if (num != null) {
                    phoneCandidates.add(num)
                }

                // 8 ou 9 dígitos: telefone local sem DDD -> tentar com DDD 13 padrão e DDI 55 + DDD 13
                if (digits.length in 8..9) {
                    "13$digits".toLongOrNull()?.let { phoneCandidates.add(it) }
                    "5513$digits".toLongOrNull()?.let { phoneCandidates.add(it) }
                }
                // 10 ou 11 dígitos (padrão nacional brasileiro com DDD) -> tentar com DDI 55
                else if (digits.length in 10..11) {
                    "55$digits".toLongOrNull()?.let { phoneCandidates.add(it) }
                }
                // 12 ou 13 dígitos começando com DDI 55 -> tentar sem o 55
                else if (digits.startsWith("55") && digits.length in 12..13) {
                    digits.substring(2).toLongOrNull()?.let { phoneCandidates.add(it) }
                }
            }
            // Caso contrário, busca textual normal
            else -> {
                id = null
            }
        }

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
                phoneCandidates = phoneCandidates.ifEmpty { null },
                query = textQuery,
                startDate = range?.first,
                endDate = range?.second
            )

            result.onSuccess { list ->
                _uiState.update { it.copy(pedidos = list.distinctBy { p -> p.id }, isLoading = false) }
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
