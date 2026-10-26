package com.dutradev.carimbosocian.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dutradev.carimbosocian.R
import com.dutradev.carimbosocian.util.PhoneUtils

@Composable
fun PhoneWithDdiInput(
    selectedDdi: String,
    phoneDigits: String,
    onDdiChange: (String) -> Unit,
    onPhoneDigitsChange: (String) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    var showCustomDdiDialog by remember { mutableStateOf(false) }
    var customDdiInput by remember { mutableStateOf("") }

    val currentCountry = PhoneUtils.commonCountries.find { it.code == selectedDdi }
    val flag = currentCountry?.flag ?: "🌐"

    if (showCustomDdiDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDdiDialog = false },
            title = { Text(stringResource(R.string.ddi_dialog_titulo)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = customDdiInput,
                        onValueChange = { input ->
                            customDdiInput = input.filter { it.isDigit() }.take(4)
                        },
                        label = { Text("DDI") },
                        placeholder = { Text(stringResource(R.string.ddi_dialog_placeholder)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val ddi = customDdiInput.trim()
                        if (ddi.isNotBlank()) {
                            onDdiChange(ddi)
                        }
                        showCustomDdiDialog = false
                    }
                ) {
                    Text(stringResource(R.string.confirmar))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDdiDialog = false }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Seletor de DDI
        Box {
            OutlinedCard(
                onClick = { dropdownExpanded = true },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 96.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (errorMessage != null) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = flag, fontSize = 18.sp)
                    Text(
                        text = "+$selectedDdi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                PhoneUtils.commonCountries.forEach { country ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(country.flag, fontSize = 18.sp)
                                Text(
                                    text = "${stringResource(country.nameResId)} (+${country.code})",
                                    fontSize = 14.sp,
                                    fontWeight = if (country.code == selectedDdi) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        },
                        onClick = {
                            onDdiChange(country.code)
                            dropdownExpanded = false
                        }
                    )
                }

                HorizontalDivider()

                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🌐", fontSize = 18.sp)
                            Text(stringResource(R.string.pais_outro), fontSize = 14.sp)
                        }
                    },
                    onClick = {
                        dropdownExpanded = false
                        customDdiInput = if (PhoneUtils.commonCountries.none { it.code == selectedDdi }) selectedDdi else ""
                        showCustomDdiDialog = true
                    }
                )
            }
        }

        // Campo de Telefone com Máscara Dinâmica por DDI
        val formattedDisplay = PhoneUtils.formatInputForDdi(selectedDdi, phoneDigits)

        OutlinedTextField(
            value = formattedDisplay,
            onValueChange = { input ->
                val clean = input.filter { it.isDigit() }
                val maxLen = when (selectedDdi) {
                    "55" -> 11
                    "1" -> 10
                    "351" -> 9
                    "34" -> 9
                    "54" -> 11
                    else -> 15
                }
                onPhoneDigitsChange(clean.take(maxLen))
            },
            label = { Text("${stringResource(R.string.whatsapp)} *") },
            placeholder = {
                Text(PhoneUtils.getPlaceholderForDdi(selectedDdi))
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            isError = errorMessage != null,
            supportingText = errorMessage?.let { { Text(it) } }
        )
    }
}
