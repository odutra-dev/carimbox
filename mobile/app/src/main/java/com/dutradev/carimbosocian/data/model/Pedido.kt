package com.dutradev.carimbosocian.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pedido(
    val id: Long? = null,
    @SerialName("data_pedido")
    val dataPedido: String? = null,
    val cliente: String = "",
    val whatsapp: Long = 0L,
    @SerialName("tipo_modelo")
    val tipoModelo: String = "",
    @SerialName("texto_logo")
    val textoLogo: String = "",
    @SerialName("tipo_pagamento")
    val tipoPagamento: String = "",
    val valor: Double = 0.0,
    val observacao: String? = null,
    val entregue: Boolean = false,
    @SerialName("data_entrega")
    val dataEntrega: String? = null,
    val label: String = "Carimbo"
)

@Serializable
data class PedidoInsert(
    val cliente: String,
    val whatsapp: Long,
    @SerialName("tipo_modelo")
    val tipoModelo: String,
    @SerialName("texto_logo")
    val textoLogo: String,
    @SerialName("tipo_pagamento")
    val tipoPagamento: String,
    val valor: Double,
    val observacao: String? = null,
    val entregue: Boolean = false,
    @SerialName("data_entrega")
    val dataEntrega: String,
    val label: String = "Carimbo"
)

@Serializable
data class PedidoUpdate(
    val cliente: String? = null,
    val whatsapp: Long? = null,
    @SerialName("tipo_modelo")
    val tipoModelo: String? = null,
    @SerialName("texto_logo")
    val textoLogo: String? = null,
    @SerialName("tipo_pagamento")
    val tipoPagamento: String? = null,
    val valor: Double? = null,
    val observacao: String? = null,
    val entregue: Boolean? = null,
    @SerialName("data_entrega")
    val dataEntrega: String? = null,
    val label: String? = null
)
