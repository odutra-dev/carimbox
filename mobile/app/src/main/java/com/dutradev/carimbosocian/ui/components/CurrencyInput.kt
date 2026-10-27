package com.dutradev.carimbosocian.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dutradev.carimbosocian.R
import com.dutradev.carimbosocian.util.CurrencyUtils

@Composable
fun CurrencyInput(
    centsDigits: String,
    onCentsDigitsChange: (String) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    val displayValue = CurrencyUtils.formatCentsToCurrency(centsDigits)

    OutlinedTextField(
        value = displayValue,
        onValueChange = { input ->
            val digits = input.filter { it.isDigit() }
            val clean = digits.trimStart('0').take(9)
            onCentsDigitsChange(clean)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        label = { Text("${stringResource(R.string.valor)} *") },
        placeholder = { Text("R$ 0,00") },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        isError = errorMessage != null,
        supportingText = errorMessage?.let { { Text(it) } }
    )
}
