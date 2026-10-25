package com.dutradev.carimbosocian.ui.screens.ver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dutradev.carimbosocian.data.model.Pedido
import com.dutradev.carimbosocian.data.repository.PedidosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VerPedidoUiState(
    val pedido: Pedido? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isDeleted: Boolean = false
)

class VerPedidoViewModel(
    private val pedidoId: Long,
    private val repository: PedidosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerPedidoUiState())
    val uiState: StateFlow<VerPedidoUiState> = _uiState.asStateFlow()

    init {
        loadPedido()
    }

    fun loadPedido() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.getPedidoById(pedidoId)
            result.onSuccess { data ->
                _uiState.update { it.copy(pedido = data, isLoading = false) }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Não foi possível carregar o pedido"
                    )
                }
            }
        }
    }

    fun toggleStatus() {
        val current = _uiState.value.pedido ?: return
        viewModelScope.launch {
            val result = repository.toggleStatus(pedidoId, current.entregue)
            result.onSuccess {
                _uiState.update {
                    it.copy(pedido = current.copy(entregue = !current.entregue))
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(errorMessage = "Erro ao alterar status: ${err.localizedMessage}")
                }
            }
        }
    }

    fun deletePedido(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.deletePedido(pedidoId)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isDeleted = true) }
                onDeleted()
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao excluir: ${err.localizedMessage}"
                    )
                }
            }
        }
    }

    fun clearErrorMessage() = _uiState.update { it.copy(errorMessage = null) }
}
