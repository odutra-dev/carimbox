package com.dutradev.carimbosocian.ui.screens.novo

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dutradev.carimbosocian.R
import com.dutradev.carimbosocian.util.DateUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovoPedidoScreen(
    viewModel: NovoPedidoViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var paymentDropdownExpanded by remember { mutableStateOf(false) }
    var labelDropdownExpanded by remember { mutableStateOf(false) }

    val paymentOptions = listOf("Pix", "Dinheiro", "Cartão de Crédito", "Cartão de Débito", "A Combinar")
    val labelOptions = listOf("Carimbo", "Mdf/Acrílico", "Outros")

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.novo_pedido_titulo),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.novo_pedido_subtitulo),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // Cliente
            OutlinedTextField(
                value = uiState.cliente,
                onValueChange = { viewModel.onClienteChange(it) },
                label = { Text(stringResource(R.string.cliente)) },
                placeholder = { Text("Nome do cliente") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            // WhatsApp
            OutlinedTextField(
                value = uiState.whatsapp,
                onValueChange = { viewModel.onWhatsappChange(it) },
                label = { Text("${stringResource(R.string.whatsapp)} *") },
                placeholder = { Text("Ex: 11999999999") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            // Modelo
            OutlinedTextField(
                value = uiState.tipoModelo,
                onValueChange = { viewModel.onTipoModeloChange(it) },
                label = { Text(stringResource(R.string.tipo_modelo)) },
                placeholder = { Text("Ex: Trodat 3911") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            // Descrição / Logo
            OutlinedTextField(
                value = uiState.textoLogo,
                onValueChange = { viewModel.onTextoLogoChange(it) },
                label = { Text("${stringResource(R.string.texto_logo)} *") },
                placeholder = { Text("Texto que irá no carimbo...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                minLines = 3
            )

            // Tipo de Pagamento Dropdown
            ExposedDropdownMenuBox(
                expanded = paymentDropdownExpanded,
                onExpandedChange = { paymentDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.tipoPagamento,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.tipo_pagamento)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(10.dp)
                )

                ExposedDropdownMenu(
                    expanded = paymentDropdownExpanded,
                    onDismissRequest = { paymentDropdownExpanded = false }
                ) {
                    paymentOptions.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = {
                                viewModel.onTipoPagamentoChange(opt)
                                paymentDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Valor
            OutlinedTextField(
                value = uiState.valor,
                onValueChange = { viewModel.onValorChange(it) },
                label = { Text("${stringResource(R.string.valor)} *") },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            // Data de Entrega
            val cal = Calendar.getInstance().apply { time = uiState.dataEntrega }
            OutlinedTextField(
                value = DateUtils.toDisplayDateTime(DateUtils.toIsoString(uiState.dataEntrega)),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.data_entrega)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Selecionar data",
                        modifier = Modifier.clickable {
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val newDate = Calendar.getInstance().apply {
                                        set(year, month, dayOfMonth)
                                    }.time
                                    viewModel.onDataEntregaChange(newDate)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val newDate = Calendar.getInstance().apply {
                                    set(year, month, dayOfMonth)
                                }.time
                                viewModel.onDataEntregaChange(newDate)
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                shape = RoundedCornerShape(10.dp)
            )

            // Categoria / Label Dropdown
            ExposedDropdownMenuBox(
                expanded = labelDropdownExpanded,
                onExpandedChange = { labelDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.categoria)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = labelDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(10.dp)
                )

                ExposedDropdownMenu(
                    expanded = labelDropdownExpanded,
                    onDismissRequest = { labelDropdownExpanded = false }
                ) {
                    labelOptions.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = {
                                viewModel.onLabelChange(opt)
                                labelDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Observação
            OutlinedTextField(
                value = uiState.observacao,
                onValueChange = { viewModel.onObservacaoChange(it) },
                label = { Text(stringResource(R.string.observacao)) },
                placeholder = { Text("Informações adicionais...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                minLines = 2
            )

            // Switch Entregue
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.entregue),
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = uiState.entregue,
                    onCheckedChange = { viewModel.onEntregueChange(it) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botão Salvar
            Button(
                onClick = { viewModel.submit(onSuccessCallback = onNavigateBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = stringResource(R.string.salvar),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
