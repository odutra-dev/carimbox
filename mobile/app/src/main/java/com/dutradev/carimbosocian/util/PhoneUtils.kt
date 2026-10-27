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

    val brazilianDdds = setOf(
        "11", "12", "13", "14", "15", "16", "17", "18", "19",
        "21", "22", "24", "27", "28",
        "31", "32", "33", "34", "35", "37", "38",
        "41", "42", "43", "44", "45", "46", "47", "48", "49",
        "51", "53", "54", "55",
        "61", "62", "63", "64", "65", "66", "67", "68", "69",
        "71", "73", "74", "75", "77", "79",
        "81", "82", "83", "84", "85", "86", "87", "88", "89",
        "91", "92", "93", "94", "95", "96", "97", "98", "99"
    )

    fun isBrazilianFormat(digits: String): Boolean {
        if (digits.length == 11) {
            val ddd = digits.take(2)
            return ddd in brazilianDdds && digits[2] == '9'
        }
        if (digits.length == 10) {
            val ddd = digits.take(2)
            return ddd in brazilianDdds && digits[2] in '2'..'5'
        }
        return false
    }

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

    fun formatUsPhone(digits: String): String {
        val clean = digits.filter { it.isDigit() }.take(10)
        return when {
            clean.isEmpty() -> ""
            clean.length <= 3 -> "($clean"
            clean.length <= 6 -> "(${clean.substring(0, 3)}) ${clean.substring(3)}"
            else -> "(${clean.substring(0, 3)}) ${clean.substring(3, 6)}-${clean.substring(6)}"
        }
    }

    fun formatPortugalPhone(digits: String): String {
        val clean = digits.filter { it.isDigit() }.take(9)
        return when {
            clean.isEmpty() -> ""
            clean.length <= 3 -> clean
            clean.length <= 6 -> "${clean.substring(0, 3)} ${clean.substring(3)}"
            else -> "${clean.substring(0, 3)} ${clean.substring(3, 6)} ${clean.substring(6)}"
        }
    }

    fun formatSpainPhone(digits: String): String {
        val clean = digits.filter { it.isDigit() }.take(9)
        return when {
            clean.isEmpty() -> ""
            clean.length <= 3 -> clean
            clean.length <= 5 -> "${clean.substring(0, 3)} ${clean.substring(3)}"
            clean.length <= 7 -> "${clean.substring(0, 3)} ${clean.substring(3, 5)} ${clean.substring(5)}"
            else -> "${clean.substring(0, 3)} ${clean.substring(3, 5)} ${clean.substring(5, 7)} ${clean.substring(7)}"
        }
    }

    fun formatArgentinaPhone(digits: String): String {
        val clean = digits.filter { it.isDigit() }.take(11)
        return when {
            clean.isEmpty() -> ""
            clean.length <= 2 -> clean
            clean.length <= 6 -> "${clean.substring(0, 2)} ${clean.substring(2)}"
            else -> "${clean.substring(0, 2)} ${clean.substring(2, 6)}-${clean.substring(6)}"
        }
    }

    fun formatInputForDdi(ddi: String, digits: String): String {
        val clean = digits.filter { it.isDigit() }
        return when (ddi) {
            "55" -> formatBrazilianPhone(clean)
            "1" -> formatUsPhone(clean)
            "351" -> formatPortugalPhone(clean)
            "34" -> formatSpainPhone(clean)
            "54" -> formatArgentinaPhone(clean)
            else -> {
                // Formatação genérica agrupada
                when {
                    clean.length <= 4 -> clean
                    clean.length <= 8 -> "${clean.take(4)} ${clean.drop(4)}"
                    else -> "${clean.take(3)} ${clean.substring(3, 7)} ${clean.drop(7)}"
                }
            }
        }
    }

    fun getPlaceholderForDdi(ddi: String): String {
        return when (ddi) {
            "55" -> "(13) 99999-9999"
            "1" -> "(202) 555-0123"
            "351" -> "912 345 678"
            "34" -> "612 34 56 78"
            "54" -> "11 2345-6789"
            else -> "123456789"
        }
    }

    fun formatPhoneDisplay(rawPhone: Long): String {
        val s = rawPhone.toString().filter { it.isDigit() }
        if (s.isBlank() || s == "0") return ""

        // Formato brasileiro nacional padrão (10 ou 11 dígitos)
        if (isBrazilianFormat(s)) {
            return if (s.length == 11) {
                "(${s.substring(0, 2)}) ${s.substring(2, 7)}-${s.substring(7)}"
            } else {
                "(${s.substring(0, 2)}) ${s.substring(2, 6)}-${s.substring(6)}"
            }
        }

        // Formato brasileiro com DDI 55
        if (s.startsWith("55") && s.length in 12..13) {
            val national = s.substring(2)
            return if (national.length == 11) {
                "+55 (${national.substring(0, 2)}) ${national.substring(2, 7)}-${national.substring(7)}"
            } else {
                "+55 (${national.substring(0, 2)}) ${national.substring(2, 6)}-${national.substring(6)}"
            }
        }

        // Portugal (+351)
        if (s.startsWith("351") && s.length == 12) {
            val num = s.substring(3)
            return "+351 ${num.substring(0, 3)} ${num.substring(3, 6)} ${num.substring(6)}"
        }

        // EUA / Canadá (+1)
        if (s.startsWith("1") && s.length == 11) {
            val num = s.substring(1)
            return "+1 (${num.substring(0, 3)}) ${num.substring(3, 6)}-${num.substring(6)}"
        }

        // Espanha (+34)
        if (s.startsWith("34") && s.length == 11) {
            val num = s.substring(2)
            return "+34 ${num.substring(0, 3)} ${num.substring(3, 5)} ${num.substring(5, 7)} ${num.substring(7)}"
        }

        // Argentina (+54)
        if (s.startsWith("54")) {
            val num = s.substring(2)
            return if (num.startsWith("9") && num.length >= 10) {
                val rest = num.substring(1)
                "+54 9 ${rest.substring(0, 2)} ${rest.substring(2, 6)}-${rest.substring(6)}"
            } else if (num.length >= 10) {
                "+54 ${num.substring(0, 2)} ${num.substring(2, 6)}-${num.substring(6)}"
            } else {
                "+54 $num"
            }
        }

        // Outros países internacionais
        val known = commonCountries.find { s.startsWith(it.code) }
        return if (known != null) {
            "+${known.code} ${s.removePrefix(known.code)}"
        } else {
            "+$s"
        }
    }

    fun getWhatsAppTargetPhone(rawPhone: Long): String {
        val s = rawPhone.toString().filter { it.isDigit() }
        if (s.isBlank() || s == "0") return ""

        if (s.startsWith("55") && s.length in 12..13) {
            return s
        }

        if (isBrazilianFormat(s)) {
            return "55$s"
        }

        // Número internacional já inclui o DDI
        return s
    }

    fun parseStoredPhone(rawPhone: Long): Pair<String, String> {
        val s = rawPhone.toString().filter { it.isDigit() }
        if (s.isBlank() || s == "0") return Pair("55", "")

        // Prefixo 55 explícito (12 ou 13 dígitos)
        if (s.startsWith("55") && s.length in 12..13) {
            return Pair("55", s.substring(2))
        }

        // Formato brasileiro nacional padrão (10 ou 11 dígitos com DDD válido)
        if (isBrazilianFormat(s)) {
            return Pair("55", s)
        }

        // Portugal (351)
        if (s.startsWith("351")) {
            return Pair("351", s.removePrefix("351"))
        }

        // Espanha (34)
        if (s.startsWith("34")) {
            return Pair("34", s.removePrefix("34"))
        }

        // Argentina (54)
        if (s.startsWith("54")) {
            return Pair("54", s.removePrefix("54"))
        }

        // EUA / Canadá (1)
        if (s.startsWith("1") && s.length == 11) {
            return Pair("1", s.removePrefix("1"))
        }

        // Qualquer outro DDI conhecido
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
        return when (ddi) {
            "55" -> isBrazilianFormat(clean)
            "1" -> clean.length == 10
            "351" -> clean.length == 9
            "34" -> clean.length == 9
            "54" -> clean.length in 10..11
            else -> clean.length in 6..15
        }
    }
}
