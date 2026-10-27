package com.dutradev.carimbosocian.ui.screens.novo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dutradev.carimbosocian.R
import com.dutradev.carimbosocian.data.model.PedidoInsert
import com.dutradev.carimbosocian.data.repository.PedidosRepository
import com.dutradev.carimbosocian.util.CurrencyUtils
import com.dutradev.carimbosocian.util.DateUtils
import com.dutradev.carimbosocian.util.PhoneUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class NovoPedidoUiState(
    val cliente: String = "",
    val selectedDdi: String = "55",
    val whatsapp: String = "",
    val tipoModelo: String = "",
    val textoLogo: String = "",
    val tipoPagamento: String = "Pix",
    val valor: String = "",
    val observacao: String = "",
    val entregue: Boolean = false,
    val dataEntrega: Date = Date(),
    val label: String = "Carimbo",
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val whatsappErrorResId: Int? = null,
    val textoLogoErrorResId: Int? = null,
    val valorErrorResId: Int? = null
)

class NovoPedidoViewModel(
    private val repository: PedidosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NovoPedidoUiState())
    val uiState: StateFlow<NovoPedidoUiState> = _uiState.asStateFlow()

    fun onClienteChange(value: String) = _uiState.update { it.copy(cliente = value) }

    fun onDdiChange(ddi: String) = _uiState.update {
        it.copy(selectedDdi = ddi, whatsappErrorResId = null)
    }

    fun onWhatsappChange(value: String) = _uiState.update {
        it.copy(whatsapp = value, whatsappErrorResId = null)
    }

    fun onTipoModeloChange(value: String) = _uiState.update { it.copy(tipoModelo = value) }

    fun onTextoLogoChange(value: String) = _uiState.update {
        it.copy(textoLogo = value, textoLogoErrorResId = null)
    }

    fun onTipoPagamentoChange(value: String) = _uiState.update { it.copy(tipoPagamento = value) }

    fun onValorChange(value: String) = _uiState.update {
        it.copy(valor = value, valorErrorResId = null)
    }

    fun onObservacaoChange(value: String) = _uiState.update { it.copy(observacao = value) }

    fun onEntregueChange(value: Boolean) = _uiState.update { it.copy(entregue = value) }

    fun onDataEntregaChange(value: Date) = _uiState.update { it.copy(dataEntrega = value) }

    fun onLabelChange(value: String) = _uiState.update { it.copy(label = value) }

    fun submit(onSuccessCallback: () -> Unit) {
        val state = _uiState.value

        val whatsappDigits = state.whatsapp.filter { it.isDigit() }
        val isWhatsappValid = PhoneUtils.isValidPhone(state.selectedDdi, whatsappDigits)
        val whatsappError = when {
            whatsappDigits.isBlank() -> R.string.erro_whatsapp_obrigatorio
            !isWhatsappValid -> R.string.erro_whatsapp_invalido
            else -> null
        }

        val textoLogoError = if (state.textoLogo.trim().isBlank()) {
            R.string.erro_texto_logo_obrigatorio
        } else null

        val valorDouble = CurrencyUtils.centsToDouble(state.valor)
        val valorError = if (valorDouble <= 0.0) {
            R.string.erro_valor_obrigatorio
        } else null

        if (whatsappError != null || textoLogoError != null || valorError != null) {
            _uiState.update {
                it.copy(
                    whatsappErrorResId = whatsappError,
                    textoLogoErrorResId = textoLogoError,
                    valorErrorResId = valorError,
                    errorMessageResId = R.string.alerta_campos_obrigatorios
                )
            }
            return
        }

        val finalPhone = PhoneUtils.combineDdiAndNumber(state.selectedDdi, whatsappDigits)

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessageResId = null,
                errorMessage = null,
                whatsappErrorResId = null,
                textoLogoErrorResId = null,
                valorErrorResId = null
            )
        }

        viewModelScope.launch {
            val insert = PedidoInsert(
                cliente = state.cliente.trim(),
                whatsapp = finalPhone,
                tipoModelo = state.tipoModelo.trim(),
                textoLogo = state.textoLogo.trim(),
                tipoPagamento = state.tipoPagamento,
                valor = valorDouble,
                observacao = state.observacao.trim().ifBlank { null },
                entregue = state.entregue,
                dataEntrega = DateUtils.toIsoString(state.dataEntrega),
                label = state.label
            )

            val result = repository.insertPedido(insert)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                onSuccessCallback()
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage
                    )
                }
            }
        }
    }

    fun clearErrorMessage() = _uiState.update { it.copy(errorMessage = null, errorMessageResId = null) }
}
