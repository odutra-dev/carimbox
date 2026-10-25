package com.dutradev.carimbosocian.ui.screens.atualizar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dutradev.carimbosocian.data.model.PedidoUpdate
import com.dutradev.carimbosocian.data.repository.PedidosRepository
import com.dutradev.carimbosocian.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class AtualizarPedidoUiState(
    val cliente: String = "",
    val whatsapp: String = "",
    val tipoModelo: String = "",
    val textoLogo: String = "",
    val tipoPagamento: String = "Pix",
    val valor: String = "",
    val observacao: String = "",
    val entregue: Boolean = false,
    val dataEntrega: Date = Date(),
    val label: String = "Carimbo",
    val isLoadingData: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class AtualizarPedidoViewModel(
    private val pedidoId: Long,
    private val repository: PedidosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AtualizarPedidoUiState())
    val uiState: StateFlow<AtualizarPedidoUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingData = true, errorMessage = null) }
            val result = repository.getPedidoById(pedidoId)
            result.onSuccess { pedido ->
                _uiState.update {
                    it.copy(
                        cliente = pedido.cliente,
                        whatsapp = pedido.whatsapp.toString(),
                        tipoModelo = pedido.tipoModelo,
                        textoLogo = pedido.textoLogo,
                        tipoPagamento = pedido.tipoPagamento.ifBlank { "Pix" },
                        valor = pedido.valor.toString(),
                        observacao = pedido.observacao ?: "",
                        entregue = pedido.entregue,
                        label = pedido.label.ifBlank { "Carimbo" },
                        isLoadingData = false
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoadingData = false,
                        errorMessage = err.localizedMessage ?: "Erro ao carregar dados do pedido"
                    )
                }
            }
        }
    }

    fun onClienteChange(value: String) = _uiState.update { it.copy(cliente = value) }
    fun onWhatsappChange(value: String) = _uiState.update { it.copy(whatsapp = value) }
    fun onTipoModeloChange(value: String) = _uiState.update { it.copy(tipoModelo = value) }
    fun onTextoLogoChange(value: String) = _uiState.update { it.copy(textoLogo = value) }
    fun onTipoPagamentoChange(value: String) = _uiState.update { it.copy(tipoPagamento = value) }
    fun onValorChange(value: String) = _uiState.update { it.copy(valor = value) }
    fun onObservacaoChange(value: String) = _uiState.update { it.copy(observacao = value) }
    fun onEntregueChange(value: Boolean) = _uiState.update { it.copy(entregue = value) }
    fun onDataEntregaChange(value: Date) = _uiState.update { it.copy(dataEntrega = value) }
    fun onLabelChange(value: String) = _uiState.update { it.copy(label = value) }

    fun submit(onSuccessCallback: () -> Unit) {
        val state = _uiState.value

        val whatsappDigits = state.whatsapp.filter { it.isDigit() }
        val valorParsed = state.valor.replace(",", ".").toDoubleOrNull()

        if (whatsappDigits.isBlank() || state.textoLogo.isBlank() || valorParsed == null) {
            _uiState.update {
                it.copy(errorMessage = "Preencha os campos obrigatórios (*): WhatsApp, Descrição e Valor válido.")
            }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            val update = PedidoUpdate(
                cliente = state.cliente.trim(),
                whatsapp = whatsappDigits.toLongOrNull(),
                tipoModelo = state.tipoModelo.trim(),
                textoLogo = state.textoLogo.trim(),
                tipoPagamento = state.tipoPagamento,
                valor = valorParsed,
                observacao = state.observacao.trim().ifBlank { null },
                entregue = state.entregue,
                dataEntrega = DateUtils.toIsoString(state.dataEntrega),
                label = state.label
            )

            val result = repository.updatePedido(pedidoId, update)

            result.onSuccess {
                _uiState.update { it.copy(isSaving = false, isSuccess = true) }
                onSuccessCallback()
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = err.localizedMessage ?: "Erro ao atualizar pedido"
                    )
                }
            }
        }
    }

    fun clearErrorMessage() = _uiState.update { it.copy(errorMessage = null) }
}
