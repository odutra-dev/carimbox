package com.dutradev.carimbosocian.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dutradev.carimbosocian.R
import com.dutradev.carimbosocian.data.model.Pedido
import com.dutradev.carimbosocian.ui.theme.StatusDelivered
import com.dutradev.carimbosocian.util.DateUtils
import com.dutradev.carimbosocian.util.IntentUtils

@Composable
fun PedidoCard(
    pedido: Pedido,
    onClick: () -> Unit,
    onToggleStatus: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }

    val labelColor = when (pedido.label) {
        "Carimbo" -> Color(0xFF0284C7)
        "Mdf/Acrílico" -> Color(0xFF8B5CF6)
        "Outros" -> Color(0xFF10B981)
        else -> Color(0xFF64748B)
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = if (pedido.entregue) {
                        stringResource(R.string.marcar_como_pendente)
                    } else {
                        stringResource(R.string.marcar_como_concluido)
                    }
                )
            },
            text = {
                Text("Deseja alterar o status do Pedido #${pedido.id}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        pedido.id?.let { id ->
                            onToggleStatus(id, pedido.entregue)
                        }
                    }
                ) {
                    Text(stringResource(R.string.confirmar))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Faixa lateral colorida com base na Categoria/Label
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(labelColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pedido #${pedido.id ?: 0}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(isDelivered = pedido.entregue)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (pedido.cliente.isNotBlank()) pedido.cliente else "Sem nome de cliente",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Data: ${DateUtils.toDisplayDateTime(pedido.dataPedido)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    if (pedido.dataEntrega != null) {
                        Text(
                            text = "Entrega: ${DateUtils.toDisplayDate(pedido.dataEntrega)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!pedido.entregue) {
                        IconButton(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Concluir",
                                tint = StatusDelivered,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            val msg = buildWhatsAppMessage(pedido)
                            IntentUtils.openWhatsApp(context, pedido.whatsapp)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "WhatsApp",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun buildWhatsAppMessage(pedido: Pedido): String {
    return """
        📦 Nº DO PEDIDO: #${pedido.id ?: 0}
        ${if (pedido.cliente.isNotBlank()) "👤 Cliente: ${pedido.cliente}" else ""}
        ${if (pedido.tipoModelo.isNotBlank()) "🔖 Modelo: ${pedido.tipoModelo}" else ""}
        ✏ Descrição: ${pedido.textoLogo}
        ${if (!pedido.observacao.isNullOrBlank()) "📝 Obs: ${pedido.observacao}" else ""}
        💰 Valor: ${IntentUtils.formatCurrency(pedido.valor)}
        📅 Data de entrega: ${DateUtils.toDisplayDateTime(pedido.dataEntrega)}
    """.trimIndent()
}
