package com.dutradev.carimbosocian.ui.screens.ver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dutradev.carimbosocian.R
import com.dutradev.carimbosocian.ui.components.InfoRow
import com.dutradev.carimbosocian.ui.components.StatusBadge
import com.dutradev.carimbosocian.ui.theme.StatusDelivered
import com.dutradev.carimbosocian.ui.theme.StatusPending
import com.dutradev.carimbosocian.util.DateUtils
import com.dutradev.carimbosocian.util.IntentUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerPedidoScreen(
    viewModel: VerPedidoViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadPedido()
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearErrorMessage()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.excluir_pedido)) },
            text = {
                Text(stringResource(R.string.excluir_pedido_confirmacao, uiState.pedido?.id ?: 0))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deletePedido(onDeleted = onNavigateBack)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.excluir),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.pedido_id, uiState.pedido?.id ?: 0),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.voltar)
                        )
                    }
                },
                actions = {
                    uiState.pedido?.id?.let { id ->
                        IconButton(onClick = { onNavigateToEdit(id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.editar),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.excluir),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.pedido == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.nenhum_pedido_encontrado),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            val pedido = uiState.pedido!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card Principal de Detalhes
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.detalhes_pedido),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(isDelivered = pedido.entregue)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow(
                            label = stringResource(R.string.cliente),
                            value = pedido.cliente,
                            icon = Icons.Default.Person
                        )

                        InfoRow(
                            label = stringResource(R.string.whatsapp),
                            value = pedido.whatsapp.toString(),
                            icon = Icons.Default.Phone,
                            enableCopy = true,
                            onClick = { IntentUtils.openWhatsApp(context, pedido.whatsapp) }
                        )

                        InfoRow(
                            label = stringResource(R.string.tipo_modelo),
                            value = pedido.tipoModelo,
                            icon = Icons.Default.Label
                        )

                        InfoRow(
                            label = stringResource(R.string.texto_logo),
                            value = pedido.textoLogo,
                            icon = Icons.Default.Description,
                            enableCopy = true
                        )

                        InfoRow(
                            label = stringResource(R.string.tipo_pagamento),
                            value = pedido.tipoPagamento,
                            icon = Icons.Default.Payments
                        )

                        InfoRow(
                            label = stringResource(R.string.valor),
                            value = IntentUtils.formatCurrency(pedido.valor),
                            icon = Icons.Default.Payments
                        )

                        InfoRow(
                            label = stringResource(R.string.data_pedido),
                            value = DateUtils.toDisplayDateTime(pedido.dataPedido),
                            icon = Icons.Default.CalendarToday
                        )

                        InfoRow(
                            label = stringResource(R.string.data_entrega),
                            value = DateUtils.toDisplayDateTime(pedido.dataEntrega),
                            icon = Icons.Default.CalendarToday
                        )

                        if (!pedido.observacao.isNullOrBlank()) {
                            InfoRow(
                                label = stringResource(R.string.observacao),
                                value = pedido.observacao,
                                icon = Icons.Default.Description
                            )
                        }
                    }
                }

                // Ação de Alterar Status
                OutlinedButton(
                    onClick = { viewModel.toggleStatus() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (pedido.entregue) StatusPending else StatusDelivered
                    )
                ) {
                    Text(
                        text = if (pedido.entregue) {
                            stringResource(R.string.reabrir_pedido)
                        } else {
                            stringResource(R.string.concluir_pedido)
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Botão Copiar Todos os Dados
                Button(
                    onClick = {
                        val fullSummary = """
Pedido #${pedido.id}
${if (pedido.cliente.isNotBlank()) "Cliente: ${pedido.cliente}" else ""}
WhatsApp: ${pedido.whatsapp}
${if (pedido.tipoModelo.isNotBlank()) "Modelo: ${pedido.tipoModelo}" else ""}
Descrição/Logo: ${pedido.textoLogo}
Pagamento: ${pedido.tipoPagamento}
Valor: ${IntentUtils.formatCurrency(pedido.valor)}
Data do Pedido: ${DateUtils.toDisplayDateTime(pedido.dataPedido)}
Data de Entrega: ${DateUtils.toDisplayDateTime(pedido.dataEntrega)}
${if (!pedido.observacao.isNullOrBlank()) "Observação: ${pedido.observacao}" else ""}
Status: ${if (pedido.entregue) "Concluído" else "Pendente"}
                        """.trimIndent()

                        IntentUtils.copyToClipboard(
                            context = context,
                            label = "Pedido #${pedido.id}",
                            text = fullSummary,
                            feedbackMessage = context.getString(R.string.dados_copiados)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.copiar_todos))
                }

                // Botão Conversar no WhatsApp
                Button(
                    onClick = {
                        val msg = """
Olá ${if (pedido.cliente.isNotBlank()) pedido.cliente else ""}! 😄
Aqui é da Carimbos Ocian a respeito do seu Pedido #${pedido.id}.
                        """.trimIndent()
                        IntentUtils.openWhatsApp(context, pedido.whatsapp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorGreenWhatsapp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.abrir_whatsapp))
                }
            }
        }
    }
}

private val ColorGreenWhatsapp = androidx.compose.ui.graphics.Color(0xFF25D366)
