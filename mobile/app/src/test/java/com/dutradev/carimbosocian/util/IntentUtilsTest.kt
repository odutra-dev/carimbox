package com.dutradev.carimbosocian.util

import com.dutradev.carimbosocian.testutil.PedidoTestFactory
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IntentUtilsTest {

    @Test
    fun `buildMensagemNovoPedido includes all details when full pedido is provided`() {
        val pedido = PedidoTestFactory.createPedido(
            id = 105L,
            tipoModelo = "Nykon 302",
            textoLogo = "Dra. Ana Paula - Pediatra",
            observacao = "Arte aprovada no balcão",
            tipoPagamento = "Cartão de Crédito",
            valor = 55.50,
            dataEntrega = "2026-11-10T14:30:00Z"
        )

        val message = IntentUtils.buildMensagemNovoPedido(pedido)

        assertTrue(message.contains("*Nº: 105*"))
        assertTrue(message.contains("🔖 Modelo: Nykon 302"))
        assertTrue(message.contains("Dra. Ana Paula - Pediatra"))
        assertTrue(message.contains("📝 Observação: Arte aprovada no balcão"))
        assertTrue(message.contains("💰 Forma de pagamento: Cartão de Crédito"))
        assertTrue(message.contains("💵 Valor total: R$ 55,50"))
        assertTrue(message.contains("📦 RETIRADA A PARTIR DE"))
        assertTrue(message.contains("📍 *RETIRADA: DENTRO do CHAVEIRO OCIAN*"))
    }

    @Test
    fun `buildMensagemNovoPedido omits modelo and observacao when blank or null`() {
        val pedido = PedidoTestFactory.createPedido(
            id = null,
            tipoModelo = "",
            textoLogo = "Carimbo Simples 1 linha",
            observacao = null,
            tipoPagamento = "Pix",
            valor = 25.0
        )

        val message = IntentUtils.buildMensagemNovoPedido(pedido)

        assertTrue(message.contains("*Nº: 0*"))
        assertFalse(message.contains("🔖 Modelo:"))
        assertTrue(message.contains("Carimbo Simples 1 linha"))
        assertFalse(message.contains("📝 Observação:"))
        assertTrue(message.contains("💰 Forma de pagamento: Pix"))
        assertTrue(message.contains("💵 Valor total: R$ 25,00"))
    }

    @Test
    fun `buildMensagemPedidoConcluido includes correct order id and pickup information`() {
        val pedido = PedidoTestFactory.createPedido(id = 240L)
        val message = IntentUtils.buildMensagemPedidoConcluido(pedido)

        assertTrue(message.contains("📦 Retirada com o nº do pedido: 240"))
        assertTrue(message.contains("✅ Seu pedido já foi finalizado e está disponível para retirada! ✅"))
        assertTrue(message.contains("📍 *RETIRADA: DENTRO do CHAVEIRO OCIAN*"))
        assertTrue(message.contains("https://maps.app.goo.gl/3FzpDGgtgv9ipG1f9"))
    }

    @Test
    fun `formatCurrency formats double value to currency string`() {
        val formatted = IntentUtils.formatCurrency(79.90)
            .replace('\u00A0', ' ')
            .replace('\u202F', ' ')

        assertTrue(formatted.contains("R$"))
        assertTrue(formatted.contains("79,90"))
    }
}
