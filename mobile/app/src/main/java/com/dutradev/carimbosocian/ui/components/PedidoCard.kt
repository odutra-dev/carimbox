package com.dutradev.carimbosocian.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.dutradev.carimbosocian.ui.theme.LabelCarimbo
import com.dutradev.carimbosocian.ui.theme.LabelDefault
import com.dutradev.carimbosocian.ui.theme.LabelMdf
import com.dutradev.carimbosocian.ui.theme.LabelOuthers
import com.dutradev.carimbosocian.ui.theme.StatusDelivered
import com.dutradev.carimbosocian.util.DateUtils
import com.dutradev.carimbosocian.util.IntentUtils
import com.dutradev.carimbosocian.util.PhoneUtils

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

                    val formattedPhone = PhoneUtils.formatPhoneDisplay(pedido.whatsapp)
                    Text(
                        text = if (pedido.cliente.isNotBlank()) "${pedido.cliente} • $formattedPhone" else formattedPhone,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botão Marcar como Concluído (se não estiver entregue)
                    if (!pedido.entregue) {
                        Surface(
                            onClick = { showConfirmDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            color = StatusDelivered.copy(alpha = 0.14f),
                            border = BorderStroke(1.5.dp, StatusDelivered.copy(alpha = 0.45f)),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = stringResource(R.string.marcar_como_concluido_titulo),
                                    tint = StatusDelivered,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    // Botão Enviar WhatsApp (com destaque visual e cor oficial)
                    val whatsappGreen = Color(0xFF25D366)
                    val whatsappIconColor = Color(0xFF1EBE5D)
                    Surface(
                        onClick = {
                            val message = if (pedido.entregue) {
                                IntentUtils.buildMensagemPedidoConcluido(pedido)
                            } else {
                                IntentUtils.buildMensagemNovoPedido(pedido)
                            }
                            IntentUtils.openWhatsApp(
                                context = context,
                                rawPhone = pedido.whatsapp,
                                message = message
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = whatsappGreen.copy(alpha = 0.14f),
                        border = BorderStroke(1.5.dp, whatsappGreen.copy(alpha = 0.45f)),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = stringResource(R.string.abrir_whatsapp),
                                tint = whatsappIconColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
