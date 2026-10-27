package com.dutradev.carimbosocian.data.repository

import com.dutradev.carimbosocian.data.model.Pedido
import com.dutradev.carimbosocian.data.model.PedidoInsert
import com.dutradev.carimbosocian.data.model.PedidoUpdate

interface PedidosRepository {
    suspend fun getPedidos(
        statusFilter: String? = null, // "pendente", "concluido", or null ("todos")
        startDate: String? = null,
        endDate: String? = null,
        page: Int = 0,
        pageSize: Int = 20
    ): Result<List<Pedido>>

    suspend fun getPedidoById(id: Long): Result<Pedido>

    suspend fun searchPedidos(
        id: Long? = null,
        phone: Long? = null,
        query: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): Result<List<Pedido>>

    suspend fun insertPedido(pedido: PedidoInsert): Result<Unit>

    suspend fun updatePedido(id: Long, pedido: PedidoUpdate): Result<Unit>

    suspend fun toggleStatus(id: Long, currentStatus: Boolean): Result<Unit>

    suspend fun deletePedido(id: Long): Result<Unit>
}
