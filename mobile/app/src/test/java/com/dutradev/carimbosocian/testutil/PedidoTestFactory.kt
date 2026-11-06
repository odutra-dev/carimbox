package com.dutradev.carimbosocian.testutil

import com.dutradev.carimbosocian.data.model.Pedido
import com.dutradev.carimbosocian.data.model.PedidoInsert
import com.dutradev.carimbosocian.data.model.PedidoUpdate

object PedidoTestFactory {

    fun createPedido(
        id: Long? = 1L,
        cliente: String = "João da Silva",
        whatsapp: Long = 5513999998888L,
        tipoModelo: String = "Automático Nykon 302",
        textoLogo: String = "Advogado OAB 12345",
        tipoPagamento: String = "Pix",
        valor: Double = 45.0,
        observacao: String? = "Retirar à tarde",
        entregue: Boolean = false,
        dataEntrega: String? = "2026-11-10T14:00:00Z",
        dataPedido: String? = "2026-11-06T10:00:00Z",
        label: String = "Carimbo"
    ): Pedido = Pedido(
        id = id,
        cliente = cliente,
        whatsapp = whatsapp,
        tipoModelo = tipoModelo,
        textoLogo = textoLogo,
        tipoPagamento = tipoPagamento,
        valor = valor,
        observacao = observacao,
        entregue = entregue,
        dataEntrega = dataEntrega,
        dataPedido = dataPedido,
        label = label
    )

    fun createPedidoInsert(
        cliente: String = "Maria Souza",
        whatsapp: Long = 5513988887777L,
        tipoModelo: String = "Madeira 40x20",
        textoLogo: String = "Médica CRM 54321",
        tipoPagamento: String = "Dinheiro",
        valor: Double = 30.0,
        observacao: String? = null,
        entregue: Boolean = false,
        dataEntrega: String = "2026-11-12T16:00:00Z",
        label: String = "Carimbo"
    ): PedidoInsert = PedidoInsert(
        cliente = cliente,
        whatsapp = whatsapp,
        tipoModelo = tipoModelo,
        textoLogo = textoLogo,
        tipoPagamento = tipoPagamento,
        valor = valor,
        observacao = observacao,
        entregue = entregue,
        dataEntrega = dataEntrega,
        label = label
    )

    fun createPedidoUpdate(
        cliente: String? = null,
        whatsapp: Long? = null,
        tipoModelo: String? = null,
        textoLogo: String? = null,
        tipoPagamento: String? = null,
        valor: Double? = null,
        observacao: String? = null,
        entregue: Boolean? = null,
        dataEntrega: String? = null,
        label: String? = null
    ): PedidoUpdate = PedidoUpdate(
        cliente = cliente,
        whatsapp = whatsapp,
        tipoModelo = tipoModelo,
        textoLogo = textoLogo,
        tipoPagamento = tipoPagamento,
        valor = valor,
        observacao = observacao,
        entregue = entregue,
        dataEntrega = dataEntrega,
        label = label
    )
}
