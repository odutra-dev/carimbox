package com.dutradev.carimbosocian.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val ptBrLocale = Locale("pt", "BR")

    fun formatCentsToCurrency(centsDigits: String): String {
        val clean = centsDigits.filter { it.isDigit() }
        val cents = clean.toLongOrNull() ?: 0L
        val amount = cents / 100.0
        return NumberFormat.getCurrencyInstance(ptBrLocale).format(amount)
    }

    fun centsToDouble(centsDigits: String): Double {
        val clean = centsDigits.filter { it.isDigit() }
        val cents = clean.toLongOrNull() ?: 0L
        return cents / 100.0
    }

    fun doubleToCentsDigits(value: Double): String {
        val cents = Math.round(value * 100)
        return if (cents > 0) cents.toString() else ""
    }

    fun formatDoubleToCurrency(value: Double): String {
        return NumberFormat.getCurrencyInstance(ptBrLocale).format(value)
    }
}
