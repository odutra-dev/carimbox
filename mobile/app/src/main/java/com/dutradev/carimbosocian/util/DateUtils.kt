package com.dutradev.carimbosocian.util

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

object DateUtils {

    private val ptBrLocale = Locale("pt", "BR")
    private val defaultZone: ZoneId get() = ZoneId.systemDefault()

    // Formatadores imutáveis e 100% thread-safe
    private val isoInstantFormatter = DateTimeFormatter.ISO_INSTANT
    private val displayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", ptBrLocale)
    private val displayDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", ptBrLocale)
    private val displayTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", ptBrLocale)
    private val monthYearFormatter = DateTimeFormatter.ofPattern("MMM/yyyy", ptBrLocale)
    private val yearFormatter = DateTimeFormatter.ofPattern("yyyy", ptBrLocale)
    private val queryDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

    fun toIsoString(date: Date): String {
        return isoInstantFormatter.format(date.toInstant())
    }

    fun toDisplayDate(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "-"
        return try {
            val zdt = parseToZonedDateTime(isoString)
            displayDateFormatter.format(zdt)
        } catch (_: Exception) {
            isoString.take(10)
        }
    }

    fun toDisplayDateTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "-"
        return try {
            val zdt = parseToZonedDateTime(isoString)
            displayDateTimeFormatter.format(zdt)
        } catch (_: Exception) {
            isoString
        }
    }

    fun toDeliveryDisplay(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "-"
        return try {
            val zdt = parseToZonedDateTime(isoString)
            val d = displayDateFormatter.format(zdt)
            val t = displayTimeFormatter.format(zdt)
            "$d às $t"
        } catch (_: Exception) {
            isoString
        }
    }

    fun toDisplayDateOnly(date: Date): String {
        val zdt = date.toInstant().atZone(defaultZone)
        return displayDateFormatter.format(zdt)
    }

    fun toDisplayTimeOnly(date: Date): String {
        val zdt = date.toInstant().atZone(defaultZone)
        return displayTimeFormatter.format(zdt)
    }

    fun formatFilterLabel(type: String, date: Date): String {
        val zdt = date.toInstant().atZone(defaultZone)
        return when (type) {
            "dia" -> displayDateFormatter.format(zdt)
            "mes" -> monthYearFormatter.format(zdt).replaceFirstChar { it.uppercase() }
            "ano" -> yearFormatter.format(zdt)
            else -> ""
        }
    }

    fun updateDate(current: Date, year: Int, month: Int, dayOfMonth: Int): Date {
        val zdt = current.toInstant().atZone(defaultZone)
        // DatePickerDialog retorna mês 0..11, java.time usa 1..12
        // LocalDate.of valida ano, mês e dia atomicamente, prevenindo exceções em meses com 28/30/31 dias
        val localDate = LocalDate.of(year, month + 1, dayOfMonth)
        val updated = zdt.with(localDate)
        return Date.from(updated.toInstant())
    }

    fun updateTime(current: Date, hourOfDay: Int, minute: Int): Date {
        val zdt = current.toInstant().atZone(defaultZone)
        val localTime = LocalTime.of(hourOfDay, minute, 0, 0)
        val updated = zdt.with(localTime)
        return Date.from(updated.toInstant())
    }

    fun showDatePicker(
        context: Context,
        initialDate: Date,
        onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit
    ) {
        val activity = context.findActivity() ?: context
        val cal = Calendar.getInstance().apply { time = initialDate }
        try {
            DatePickerDialog(
                activity,
                { _, year, month, dayOfMonth ->
                    onDateSelected(year, month, dayOfMonth)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        } catch (e: Throwable) {
            Log.e("DateUtils", "Erro ao exibir DatePickerDialog", e)
        }
    }

    fun showTimePicker(
        context: Context,
        initialDate: Date,
        onTimeSelected: (hourOfDay: Int, minute: Int) -> Unit
    ) {
        val activity = context.findActivity() ?: context
        val cal = Calendar.getInstance().apply { time = initialDate }
        try {
            TimePickerDialog(
                activity,
                { _, hourOfDay, minute ->
                    onTimeSelected(hourOfDay, minute)
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        } catch (e: Throwable) {
            Log.e("DateUtils", "Erro ao exibir TimePickerDialog", e)
        }
    }

    fun parseIso(str: String?): Date? {
        if (str.isNullOrBlank()) return null
        return try {
            val zdt = parseToZonedDateTime(str)
            Date.from(zdt.toInstant())
        } catch (_: Exception) {
            null
        }
    }

    private fun parseToZonedDateTime(str: String): ZonedDateTime {
        val trimmed = str.trim()
        // 1. Tenta padrão ISO com timezone (ex: 2026-10-29T12:00:00Z ou com offset +03:00)
        try {
            return ZonedDateTime.parse(trimmed, DateTimeFormatter.ISO_DATE_TIME).withZoneSameInstant(defaultZone)
        } catch (_: Exception) {}

        // 2. Tenta parse via Instant (ex: 2026-10-29T12:00:00.000Z)
        try {
            return Instant.parse(trimmed).atZone(defaultZone)
        } catch (_: Exception) {}

        // 3. Tenta formato com espaço ou sem fuso (ex: 2026-10-29 12:00:00 ou 2026-10-29T12:00:00)
        try {
            val ldt = LocalDateTime.parse(trimmed.replace(' ', 'T'), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            return ldt.atZone(defaultZone)
        } catch (_: Exception) {}

        // 4. Tenta apenas data (ex: 2026-10-29)
        try {
            val ld = LocalDate.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE)
            return ld.atStartOfDay(defaultZone)
        } catch (_: Exception) {}

        throw IllegalArgumentException("Formato de data não reconhecido: $str")
    }

    fun getDayRange(date: Date): Pair<String, String> {
        val zdt = date.toInstant().atZone(defaultZone)
        val startOfDay = zdt.toLocalDate().atStartOfDay(defaultZone)
        val endOfDay = zdt.toLocalDate().atTime(23, 59, 59).atZone(defaultZone)

        return Pair(
            queryDateFormatter.format(startOfDay),
            queryDateFormatter.format(endOfDay)
        )
    }

    fun getMonthRange(date: Date): Pair<String, String> {
        val zdt = date.toInstant().atZone(defaultZone)
        val ym = YearMonth.from(zdt)
        val startOfMonth = ym.atDay(1).atStartOfDay(defaultZone)
        val endOfMonth = ym.atEndOfMonth().atTime(23, 59, 59).atZone(defaultZone)

        return Pair(
            queryDateFormatter.format(startOfMonth),
            queryDateFormatter.format(endOfMonth)
        )
    }

    fun getYearRange(date: Date): Pair<String, String> {
        val zdt = date.toInstant().atZone(defaultZone)
        val year = zdt.year
        val startOfYear = LocalDate.of(year, 1, 1).atStartOfDay(defaultZone)
        val endOfYear = LocalDate.of(year, 12, 31).atTime(23, 59, 59).atZone(defaultZone)

        return Pair(
            queryDateFormatter.format(startOfYear),
            queryDateFormatter.format(endOfYear)
        )
    }
}
