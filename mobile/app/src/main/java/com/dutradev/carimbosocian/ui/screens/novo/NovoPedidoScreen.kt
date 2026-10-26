package com.dutradev.carimbosocian.ui.screens.novo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dutradev.carimbosocian.R
import com.dutradev.carimbosocian.ui.components.CurrencyInput
import com.dutradev.carimbosocian.ui.components.PhoneWithDdiInput
import com.dutradev.carimbosocian.ui.util.isTabletOrLandscape
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
    val isTablet = isTabletOrLandscape()

    var paymentDropdownExpanded by remember { mutableStateOf(false) }
    var labelDropdownExpanded by remember { mutableStateOf(false) }

    val paymentOptions = listOf(
        stringResource(R.string.pagamento_pix) to "Pix",
        stringResource(R.string.pagamento_dinheiro) to "Dinheiro",
        stringResource(R.string.pagamento_credito) to "Cartão de Crédito",
        stringResource(R.string.pagamento_debito) to "Cartão de Débito",
        stringResource(R.string.pagamento_a_pagar) to "A pagar",
        stringResource(R.string.pagamento_link) to "Link de pagamento"
    )

    val labelOptions = listOf(
        stringResource(R.string.label_carimbo) to "Carimbo",
        stringResource(R.string.label_mdf) to "Mdf/Acrílico",
        stringResource(R.string.label_outhers) to "Outros"
    )

    val selectedPaymentLabel = paymentOptions.find { it.second == uiState.tipoPagamento }?.first ?: uiState.tipoPagamento
    val selectedCategoryLabel = labelOptions.find { it.second == uiState.label }?.first ?: uiState.label

    LaunchedEffect(uiState.errorMessageResId) {
        uiState.errorMessageResId?.let { resId ->
            snackbarHostState.showSnackbar(context.getString(resId))
            viewModel.clearErrorMessage()
        }
    }

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
                            contentDescription = stringResource(R.string.voltar)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 760.dp)
                    .verticalScroll(scrollState)
                    .padding(horizontal = if (isTablet) 32.dp else 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.novo_pedido_subtitulo),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                // ==============================
                // Card 1: Dados do Cliente
                // ==============================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.secao_cliente),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (isTablet) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiState.cliente,
                                    onValueChange = { viewModel.onClienteChange(it) },
                                    label = { Text(stringResource(R.string.cliente)) },
                                    placeholder = { Text(stringResource(R.string.cliente_placeholder)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )

                                PhoneWithDdiInput(
                                    selectedDdi = uiState.selectedDdi,
                                    phoneDigits = uiState.whatsapp,
                                    onDdiChange = { viewModel.onDdiChange(it) },
                                    onPhoneDigitsChange = { viewModel.onWhatsappChange(it) },
                                    errorMessage = uiState.whatsappErrorResId?.let { stringResource(it) },
                                    modifier = Modifier.weight(1.3f)
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = uiState.cliente,
                                onValueChange = { viewModel.onClienteChange(it) },
                                label = { Text(stringResource(R.string.cliente)) },
                                placeholder = { Text(stringResource(R.string.cliente_placeholder)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            PhoneWithDdiInput(
                                selectedDdi = uiState.selectedDdi,
                                phoneDigits = uiState.whatsapp,
                                onDdiChange = { viewModel.onDdiChange(it) },
                                onPhoneDigitsChange = { viewModel.onWhatsappChange(it) },
                                errorMessage = uiState.whatsappErrorResId?.let { stringResource(it) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // ==============================
                // Card 2: Detalhes do Pedido
                // ==============================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.secao_detalhes_pedido),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Categoria / Label & Tipo / Modelo
                        if (isTablet) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = labelDropdownExpanded,
                                    onExpandedChange = { labelDropdownExpanded = it },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = selectedCategoryLabel,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(stringResource(R.string.categoria_label)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Bookmarks,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = labelDropdownExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    ExposedDropdownMenu(
                                        expanded = labelDropdownExpanded,
                                        onDismissRequest = { labelDropdownExpanded = false }
                                    ) {
                                        labelOptions.forEach { opt ->
                                            DropdownMenuItem(
                                                text = { Text(opt.first) },
                                                onClick = {
                                                    viewModel.onLabelChange(opt.second)
                                                    labelDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = uiState.tipoModelo,
                                    onValueChange = { viewModel.onTipoModeloChange(it) },
                                    label = { Text(stringResource(R.string.tipo_modelo)) },
                                    placeholder = { Text(stringResource(R.string.tipo_modelo_placeholder)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Category,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                            }
                        } else {
                            ExposedDropdownMenuBox(
                                expanded = labelDropdownExpanded,
                                onExpandedChange = { labelDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedCategoryLabel,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.categoria_label)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Bookmarks,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = labelDropdownExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                ExposedDropdownMenu(
                                    expanded = labelDropdownExpanded,
                                    onDismissRequest = { labelDropdownExpanded = false }
                                ) {
                                    labelOptions.forEach { opt ->
                                        DropdownMenuItem(
                                            text = { Text(opt.first) },
                                            onClick = {
                                                viewModel.onLabelChange(opt.second)
                                                labelDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = uiState.tipoModelo,
                                onValueChange = { viewModel.onTipoModeloChange(it) },
                                label = { Text(stringResource(R.string.tipo_modelo)) },
                                placeholder = { Text(stringResource(R.string.tipo_modelo_placeholder)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        // Descrição / Logo
                        OutlinedTextField(
                            value = uiState.textoLogo,
                            onValueChange = { viewModel.onTextoLogoChange(it) },
                            label = { Text("${stringResource(R.string.texto_logo)} *") },
                            placeholder = { Text(stringResource(R.string.texto_logo_placeholder)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3,
                            isError = uiState.textoLogoErrorResId != null,
                            supportingText = uiState.textoLogoErrorResId?.let { { Text(stringResource(it)) } }
                        )

                        // Observação
                        OutlinedTextField(
                            value = uiState.observacao,
                            onValueChange = { viewModel.onObservacaoChange(it) },
                            label = { Text(stringResource(R.string.observacao)) },
                            placeholder = { Text(stringResource(R.string.observacao_placeholder)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Notes,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2
                        )
                    }
                }

                // ==============================
                // Card 3: Pagamento & Retirada
                // ==============================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.secao_pagamento_retirada),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Valor & Tipo de Pagamento
                        if (isTablet) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CurrencyInput(
                                    centsDigits = uiState.valor,
                                    onCentsDigitsChange = { viewModel.onValorChange(it) },
                                    errorMessage = uiState.valorErrorResId?.let { stringResource(it) },
                                    modifier = Modifier.weight(1f)
                                )

                                ExposedDropdownMenuBox(
                                    expanded = paymentDropdownExpanded,
                                    onExpandedChange = { paymentDropdownExpanded = it },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = selectedPaymentLabel,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(stringResource(R.string.tipo_pagamento)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.AccountBalanceWallet,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentDropdownExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    ExposedDropdownMenu(
                                        expanded = paymentDropdownExpanded,
                                        onDismissRequest = { paymentDropdownExpanded = false }
                                    ) {
                                        paymentOptions.forEach { opt ->
                                            DropdownMenuItem(
                                                text = { Text(opt.first) },
                                                onClick = {
                                                    viewModel.onTipoPagamentoChange(opt.second)
                                                    paymentDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            CurrencyInput(
                                centsDigits = uiState.valor,
                                onCentsDigitsChange = { viewModel.onValorChange(it) },
                                errorMessage = uiState.valorErrorResId?.let { stringResource(it) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            ExposedDropdownMenuBox(
                                expanded = paymentDropdownExpanded,
                                onExpandedChange = { paymentDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedPaymentLabel,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.tipo_pagamento)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentDropdownExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                ExposedDropdownMenu(
                                    expanded = paymentDropdownExpanded,
                                    onDismissRequest = { paymentDropdownExpanded = false }
                                ) {
                                    paymentOptions.forEach { opt ->
                                        DropdownMenuItem(
                                            text = { Text(opt.first) },
                                            onClick = {
                                                viewModel.onTipoPagamentoChange(opt.second)
                                                paymentDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Data e Horário de Entrega lado a lado
                        val cal = Calendar.getInstance().apply { time = uiState.dataEntrega }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Campo Data de Entrega
                            Box(modifier = Modifier.weight(1.2f)) {
                                OutlinedTextField(
                                    value = DateUtils.toDisplayDateOnly(uiState.dataEntrega),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.data_entrega)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable {
                                            DatePickerDialog(
                                                context,
                                                { _, year, month, dayOfMonth ->
                                                    val updated = DateUtils.updateDate(uiState.dataEntrega, year, month, dayOfMonth)
                                                    viewModel.onDataEntregaChange(updated)
                                                },
                                                cal.get(Calendar.YEAR),
                                                cal.get(Calendar.MONTH),
                                                cal.get(Calendar.DAY_OF_MONTH)
                                            ).show()
                                        }
                                )
                            }

                            // Campo Horário de Entrega
                            Box(modifier = Modifier.weight(0.9f)) {
                                OutlinedTextField(
                                    value = DateUtils.toDisplayTimeOnly(uiState.dataEntrega),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.horario_entrega)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable {
                                            TimePickerDialog(
                                                context,
                                                { _, hourOfDay, minute ->
                                                    val updated = DateUtils.updateTime(uiState.dataEntrega, hourOfDay, minute)
                                                    viewModel.onDataEntregaChange(updated)
                                                },
                                                cal.get(Calendar.HOUR_OF_DAY),
                                                cal.get(Calendar.MINUTE),
                                                true
                                            ).show()
                                        }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

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
                            text = stringResource(R.string.cadastrar_pedido),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
