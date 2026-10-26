package com.dutradev.carimbosocian.util

import com.dutradev.carimbosocian.R

data class CountryDdi(
    val code: String,
    val flag: String,
    val nameResId: Int
)

object PhoneUtils {
    val commonCountries = listOf(
        CountryDdi(code = "55", flag = "🇧🇷", nameResId = R.string.pais_brasil),
        CountryDdi(code = "351", flag = "🇵🇹", nameResId = R.string.pais_portugal),
        CountryDdi(code = "1", flag = "🇺🇸", nameResId = R.string.pais_eua),
        CountryDdi(code = "54", flag = "🇦🇷", nameResId = R.string.pais_argentina),
        CountryDdi(code = "34", flag = "🇪🇸", nameResId = R.string.pais_espanha)
    )

    fun formatBrazilianPhone(digits: String): String {
        val clean = digits.filter { it.isDigit() }.take(11)
        return when {
            clean.isEmpty() -> ""
            clean.length <= 2 -> "($clean"
            clean.length <= 6 -> "(${clean.substring(0, 2)}) ${clean.substring(2)}"
            clean.length <= 10 -> "(${clean.substring(0, 2)}) ${clean.substring(2, 6)}-${clean.substring(6)}"
            else -> "(${clean.substring(0, 2)}) ${clean.substring(2, 7)}-${clean.substring(7)}"
        }
    }

    fun formatPhoneDisplay(rawPhone: Long): String {
        val s = rawPhone.toString().filter { it.isDigit() }
        if (s.isBlank() || s == "0") return ""

        // Brazilian phone without 55: 10 digits or 11 digits (mobile)
        if (s.length == 11 && s[2] == '9') {
            return "(${s.substring(0, 2)}) ${s.substring(2, 7)}-${s.substring(7)}"
        }
        if (s.length == 10) {
            return "(${s.substring(0, 2)}) ${s.substring(2, 6)}-${s.substring(6)}"
        }

        // Brazilian phone with 55: 12 or 13 digits
        if (s.startsWith("55") && s.length in 12..13) {
            val national = s.substring(2)
            return if (national.length == 11) {
                "+55 (${national.substring(0, 2)}) ${national.substring(2, 7)}-${national.substring(7)}"
            } else {
                "+55 (${national.substring(0, 2)}) ${national.substring(2, 6)}-${national.substring(6)}"
            }
        }

        // Foreign phone with DDI
        return "+$s"
    }

    fun parseStoredPhone(rawPhone: Long): Pair<String, String> {
        val s = rawPhone.toString().filter { it.isDigit() }
        if (s.isBlank() || s == "0") return Pair("55", "")

        // Starts with 55 and has 12 or 13 digits
        if (s.startsWith("55") && s.length in 12..13) {
            return Pair("55", s.substring(2))
        }

        // Standard Brazilian without 55 (10 digits or 11 digits starting with 9 at 3rd pos)
        if ((s.length == 11 && s[2] == '9') || s.length == 10) {
            return Pair("55", s)
        }

        // Check common international DDIs
        val match = commonCountries.filter { it.code != "55" }.find { s.startsWith(it.code) }
        if (match != null) {
            return Pair(match.code, s.removePrefix(match.code))
        }

        return Pair("55", s)
    }

    fun combineDdiAndNumber(ddi: String, numberDigits: String): Long {
        val cleanDdi = ddi.filter { it.isDigit() }
        val cleanNum = numberDigits.filter { it.isDigit() }
        return if (cleanDdi == "55") {
            cleanNum.toLongOrNull() ?: 0L
        } else {
            "$cleanDdi$cleanNum".toLongOrNull() ?: 0L
        }
    }

    fun isValidPhone(ddi: String, numberDigits: String): Boolean {
        val clean = numberDigits.filter { it.isDigit() }
        return if (ddi == "55") {
            clean.length in 10..11
        } else {
            clean.length >= 6
        }
    }
}
