package com.dutradev.carimbosocian.ui.screens.novo

import com.dutradev.carimbosocian.R
import com.dutradev.carimbosocian.data.model.PedidoInsert
import com.dutradev.carimbosocian.data.repository.PedidosRepository
import com.dutradev.carimbosocian.testutil.MainDispatcherRule
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
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class NovoPedidoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<PedidosRepository>(relaxed = true)

    @Test
    fun `form field setters update uiState correctly`() {
        val viewModel = NovoPedidoViewModel(repository)
        val deliveryDate = Date()

        viewModel.onClienteChange("Carlos Pereira")
        viewModel.onDdiChange("351")
        viewModel.onWhatsappChange("912345678")
        viewModel.onTipoModeloChange("Colop Pocket 20")
        viewModel.onTextoLogoChange("Carlos Advogado")
        viewModel.onTipoPagamentoChange("Dinheiro")
        viewModel.onValorChange("5000")
        viewModel.onObservacaoChange("Cliente busca amanhã")
        viewModel.onEntregueChange(true)
        viewModel.onDataEntregaChange(deliveryDate)
        viewModel.onLabelChange("Personalizado")

        val state = viewModel.uiState.value
        assertEquals("Carlos Pereira", state.cliente)
        assertEquals("351", state.selectedDdi)
        assertEquals("912345678", state.whatsapp)
        assertEquals("Colop Pocket 20", state.tipoModelo)
        assertEquals("Carlos Advogado", state.textoLogo)
        assertEquals("Dinheiro", state.tipoPagamento)
        assertEquals("5000", state.valor)
        assertEquals("Cliente busca amanhã", state.observacao)
        assertTrue(state.entregue)
        assertEquals(deliveryDate, state.dataEntrega)
        assertEquals("Personalizado", state.label)
    }

    @Test
    fun `submit fails with validation errors when required fields are empty`() {
        val viewModel = NovoPedidoViewModel(repository)
        var callbackCalled = false

        viewModel.submit { callbackCalled = true }

        val state = viewModel.uiState.value
        assertEquals(R.string.erro_whatsapp_obrigatorio, state.whatsappErrorResId)
        assertEquals(R.string.erro_texto_logo_obrigatorio, state.textoLogoErrorResId)
        assertEquals(R.string.erro_valor_obrigatorio, state.valorErrorResId)
        assertEquals(R.string.alerta_campos_obrigatorios, state.errorMessageResId)
        assertFalse(callbackCalled)
        coVerify(exactly = 0) { repository.insertPedido(any()) }
    }

    @Test
    fun `submit fails when whatsapp format is invalid for the selected DDI`() {
        val viewModel = NovoPedidoViewModel(repository)
        var callbackCalled = false

        viewModel.onDdiChange("55")
        viewModel.onWhatsappChange("139999") // incompleto
        viewModel.onTextoLogoChange("Carimbo Valido")
        viewModel.onValorChange("3000")

        viewModel.submit { callbackCalled = true }

        val state = viewModel.uiState.value
        assertEquals(R.string.erro_whatsapp_invalido, state.whatsappErrorResId)
        assertFalse(callbackCalled)
        coVerify(exactly = 0) { repository.insertPedido(any()) }
    }

    @Test
    fun `submit succeeds with valid data and calls onSuccessCallback`() {
        val viewModel = NovoPedidoViewModel(repository)
        var callbackCalled = false

        viewModel.onClienteChange("Dra. Beatriz")
        viewModel.onDdiChange("55")
        viewModel.onWhatsappChange("13999998888")
        viewModel.onTipoModeloChange("Nykon 302")
        viewModel.onTextoLogoChange("Médica CRM 12345")
        viewModel.onTipoPagamentoChange("Pix")
        viewModel.onValorChange("4500") // R$ 45,00

        coEvery { repository.insertPedido(any()) } returns Result.success(Unit)

        viewModel.submit { callbackCalled = true }

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isSuccess)
        assertTrue(callbackCalled)

        coVerify(exactly = 1) {
            repository.insertPedido(
                match { insert: PedidoInsert ->
                    insert.cliente == "Dra. Beatriz" &&
                            insert.whatsapp == 13999998888L &&
                            insert.textoLogo == "Médica CRM 12345" &&
                            insert.valor == 45.0 &&
                            insert.tipoPagamento == "Pix"
                }
            )
        }
    }

    @Test
    fun `submit handles repository failure and sets errorMessage`() {
        val viewModel = NovoPedidoViewModel(repository)
        var callbackCalled = false

        viewModel.onWhatsappChange("13999998888")
        viewModel.onTextoLogoChange("Carimbo Empresa")
        viewModel.onValorChange("6000")

        coEvery { repository.insertPedido(any()) } returns Result.failure(RuntimeException("Falha ao inserir no banco"))

        viewModel.submit { callbackCalled = true }

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isSuccess)
        assertFalse(callbackCalled)
        assertEquals("Falha ao inserir no banco", state.errorMessage)
    }

    @Test
    fun `clearErrorMessage resets error message fields`() {
        val viewModel = NovoPedidoViewModel(repository)
        viewModel.submit {} // dispara erro de campos obrigatórios

        assertNotNull(viewModel.uiState.value.errorMessageResId)

        viewModel.clearErrorMessage()

        val state = viewModel.uiState.value
        assertNull(state.errorMessageResId)
        assertNull(state.errorMessage)
    }
}
