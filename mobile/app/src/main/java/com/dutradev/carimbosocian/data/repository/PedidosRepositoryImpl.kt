package com.dutradev.carimbosocian.data.repository

import com.dutradev.carimbosocian.data.model.Pedido
import com.dutradev.carimbosocian.data.model.PedidoInsert
import com.dutradev.carimbosocian.data.model.PedidoUpdate
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class PedidosRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : PedidosRepository {

    override suspend fun getPedidos(
        statusFilter: String?,
        startDate: String?,
        endDate: String?,
        page: Int,
        pageSize: Int
    ): Result<List<Pedido>> = withContext(Dispatchers.IO) {
        runCatching {
            val from = (page * pageSize).toLong()
            val to = from + pageSize - 1

            supabaseClient.from("Pedidos").select {
                order("id", Order.DESCENDING)
                range(from, to)
                filter {
                    if (statusFilter == "pendente") {
                        eq("entregue", false)
                    } else if (statusFilter == "concluido") {
                        eq("entregue", true)
                    }

                    if (!startDate.isNullOrBlank() && !endDate.isNullOrBlank()) {
                        and {
                            gte("data_pedido", startDate)
                            lte("data_pedido", endDate)
                        }
                    } else if (!startDate.isNullOrBlank()) {
                        gte("data_pedido", startDate)
                    } else if (!endDate.isNullOrBlank()) {
                        lte("data_pedido", endDate)
                    }
                }
            }.decodeList<Pedido>()
        }
    }

    override suspend fun getPedidoById(id: Long): Result<Pedido> = withContext(Dispatchers.IO) {
        runCatching {
            supabaseClient.from("Pedidos").select {
                filter {
                    eq("id", id)
                }
                single()
            }.decodeAs<Pedido>()
        }
    }

    override suspend fun searchPedidos(
        id: Long?,
        phone: Long?,
        query: String?,
        startDate: String?,
        endDate: String?
    ): Result<List<Pedido>> = withContext(Dispatchers.IO) {
        runCatching {
            supabaseClient.from("Pedidos").select {
                order("id", Order.DESCENDING)
                filter {
                    if (id != null) {
                        eq("id", id)
                    }
                    if (phone != null) {
                        val phoneStr = phone.toString()
                        val altPhone = if (phoneStr.startsWith("55") && phoneStr.length in 12..13) {
                            phoneStr.substring(2).toLongOrNull()
                        } else if (phoneStr.length in 10..11) {
                            "55$phoneStr".toLongOrNull()
                        } else {
                            null
                        }

                        if (altPhone != null) {
                            or {
                                eq("whatsapp", phone)
                                eq("whatsapp", altPhone)
                            }
                        } else {
                            eq("whatsapp", phone)
                        }
                    }
                    if (!query.isNullOrBlank()) {
                        or {
                            ilike("cliente", "%$query%")
                            ilike("texto_logo", "%$query%")
                            ilike("tipo_modelo", "%$query%")
                        }
                    }
                    if (!startDate.isNullOrBlank() && !endDate.isNullOrBlank()) {
                        and {
                            gte("data_pedido", startDate)
                            lte("data_pedido", endDate)
                        }
                    } else if (!startDate.isNullOrBlank()) {
                        gte("data_pedido", startDate)
                    } else if (!endDate.isNullOrBlank()) {
                        lte("data_pedido", endDate)
                    }
                }
            }.decodeList<Pedido>()
        }
    }

    override suspend fun insertPedido(pedido: PedidoInsert): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            supabaseClient.from("Pedidos").insert(pedido)
            Unit
        }
    }

    override suspend fun updatePedido(id: Long, pedido: PedidoUpdate): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            supabaseClient.from("Pedidos").update(pedido) {
                filter {
                    eq("id", id)
                }
            }
            Unit
        }
    }

    override suspend fun toggleStatus(id: Long, currentStatus: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val updatePayload = buildJsonObject {
                put("entregue", !currentStatus)
            }
            supabaseClient.from("Pedidos").update(updatePayload) {
                filter {
                    eq("id", id)
                }
            }
            Unit
        }
    }

    override suspend fun deletePedido(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            supabaseClient.from("Pedidos").delete {
                filter {
                    eq("id", id)
                }
            }
            Unit
        }
    }
}
