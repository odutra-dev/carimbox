package com.dutradev.carimbosocian.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val displayDateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
    private val queryDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    fun toIsoString(date: Date): String = isoFormat.format(date)

    fun toDisplayDate(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "-"
        return try {
            val date = parseAny(isoString)
            displayDateFormat.format(date)
        } catch (_: Exception) {
            isoString.take(10)
        }
    }

    fun toDisplayDateTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "-"
        return try {
            val date = parseAny(isoString)
            displayDateTimeFormat.format(date)
        } catch (_: Exception) {
            isoString
        }
    }

    fun toDeliveryDisplay(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "-"
        return try {
            val date = parseAny(isoString)
            val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            val sdfTime = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
            "${sdfDate.format(date)} às ${sdfTime.format(date)}"
        } catch (_: Exception) {
            isoString
        }
    }

    fun toDisplayDateOnly(date: Date): String {
        return displayDateFormat.format(date)
    }

    fun toDisplayTimeOnly(date: Date): String {
        val sdfTime = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
        return sdfTime.format(date)
    }

    fun formatFilterLabel(type: String, date: Date): String {
        return when (type) {
            "dia" -> SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(date)
            "mes" -> {
                val sdf = SimpleDateFormat("MMM/yyyy", Locale("pt", "BR"))
                sdf.format(date).replaceFirstChar { it.uppercase() }
            }
            "ano" -> SimpleDateFormat("yyyy", Locale("pt", "BR")).format(date)
            else -> ""
        }
    }

    fun updateDate(current: Date, year: Int, month: Int, dayOfMonth: Int): Date {
        val cal = Calendar.getInstance().apply {
            time = current
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        return cal.time
    }

    fun updateTime(current: Date, hourOfDay: Int, minute: Int): Date {
        val cal = Calendar.getInstance().apply {
            time = current
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        return cal.time
    }

    private fun parseAny(str: String): Date {
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        )
        for (pattern in patterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US)
                return sdf.parse(str) ?: continue
            } catch (_: Exception) {
                // Try next
            }
        }
        return Date()
    }

    fun getDayRange(date: Date): Pair<String, String> {
        val cal = Calendar.getInstance().apply { time = date }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = queryDateFormat.format(cal.time)

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val end = queryDateFormat.format(cal.time)

        return Pair(start, end)
    }

    fun getMonthRange(date: Date): Pair<String, String> {
        val cal = Calendar.getInstance().apply { time = date }
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val start = queryDateFormat.format(cal.time)

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val end = queryDateFormat.format(cal.time)

        return Pair(start, end)
    }

    fun getYearRange(date: Date): Pair<String, String> {
        val cal = Calendar.getInstance().apply { time = date }
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val start = queryDateFormat.format(cal.time)

        cal.set(Calendar.MONTH, Calendar.DECEMBER)
        cal.set(Calendar.DAY_OF_MONTH, 31)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val end = queryDateFormat.format(cal.time)

        return Pair(start, end)
    }
}
