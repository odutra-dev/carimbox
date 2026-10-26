package com.dutradev.carimbosocian.ui.components

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dutradev.carimbosocian.R
import com.dutradev.carimbosocian.data.model.Pedido
import com.dutradev.carimbosocian.ui.theme.LabelCarimbo
import com.dutradev.carimbosocian.ui.theme.LabelDefault
import com.dutradev.carimbosocian.ui.theme.LabelMdf
import com.dutradev.carimbosocian.ui.theme.LabelOuthers
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

    // Cores exatas originais do tema (theme/index.ts)
    val labelColor = when (pedido.label) {
        "Carimbo" -> LabelCarimbo
        "Mdf/Acrílico", "MDF/Acrílico" -> LabelMdf
        "Outros" -> LabelOuthers
        else -> LabelDefault
    }

    if (showConfirmDialog) {
        val titulo = if (pedido.entregue) {
            stringResource(R.string.marcar_como_pendente_titulo)
        } else {
            stringResource(R.string.marcar_como_concluido_titulo)
        }
        val mensagem = if (pedido.entregue) {
            stringResource(R.string.marcar_como_pendente_msg, pedido.id ?: 0)
        } else {
            stringResource(R.string.marcar_como_concluido_msg, pedido.id ?: 0)
        }

        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(titulo) },
            text = { Text(mensagem) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        pedido.id?.let { id ->
                            onToggleStatus(id, pedido.entregue)
                            if (!pedido.entregue) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.pedido_marcado_concluido),
                                    Toast.LENGTH_SHORT
                                ).show()
                                IntentUtils.openWhatsApp(
                                    context = context,
                                    rawPhone = pedido.whatsapp,
                                    message = IntentUtils.buildMensagemPedidoConcluido(pedido)
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.pedido_marcado_pendente),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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
            // Faixa lateral colorida com a cor original da Etiqueta (labelColor)
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
                    Text(
                        text = "${stringResource(R.string.pedido_id_card)} #${pedido.id ?: 0}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = DateUtils.toDisplayDateTime(pedido.dataPedido),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    val whatsappStr = pedido.whatsapp.toString()
                    val formattedPhone = formatWhatsApp(whatsappStr)
                    Text(
                        text = if (pedido.cliente.isNotBlank()) "${pedido.cliente} • $formattedPhone" else formattedPhone,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão Marcar como Concluído (se não estiver entregue)
                    if (!pedido.entregue) {
                        IconButton(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = stringResource(R.string.concluidos),
                                tint = StatusDelivered,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Botão Enviar WhatsApp (Resumo do Pedido)
                    IconButton(
                        onClick = {
                            IntentUtils.openWhatsApp(
                                context = context,
                                rawPhone = pedido.whatsapp,
                                message = IntentUtils.buildMensagemNovoPedido(pedido)
                            )
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = stringResource(R.string.abrir_whatsapp),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatWhatsApp(numero: String): String {
    val num = numero.filter { it.isDigit() }
    return when (num.length) {
        11 -> "(${num.substring(0, 2)}) ${num.substring(2, 7)}-${num.substring(7)}"
        10 -> "(${num.substring(0, 2)}) ${num.substring(2, 6)}-${num.substring(6)}"
        else -> numero
    }
}
