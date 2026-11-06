package com.dutradev.carimbosocian.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

class DateUtilsTest {

    private val defaultZone = ZoneId.systemDefault()

    @Test
    fun `toIsoString formats java Date to UTC ISO instant string`() {
        val instant = Instant.parse("2026-11-06T15:30:00Z")
        val date = Date.from(instant)
        val iso = DateUtils.toIsoString(date)

        assertEquals("2026-11-06T15:30:00Z", iso)
    }

    @Test
    fun `parseIso parses valid ISO strings and handles null or invalid gracefully`() {
        assertNull(DateUtils.parseIso(null))
        assertNull(DateUtils.parseIso(""))
        assertNull(DateUtils.parseIso("invalid-date-format"))

        val dateFromUtc = DateUtils.parseIso("2026-11-06T15:00:00Z")
        assertNotNull(dateFromUtc)

        val dateFromLocal = DateUtils.parseIso("2026-11-06T15:00:00")
        assertNotNull(dateFromLocal)

        val dateOnly = DateUtils.parseIso("2026-11-06")
        assertNotNull(dateOnly)
    }

    @Test
    fun `toDisplayDate formats ISO string to localized dd-MM-yyyy date`() {
        assertEquals("-", DateUtils.toDisplayDate(null))
        assertEquals("-", DateUtils.toDisplayDate(""))

        // Teste com data fixa na timezone local do teste
        val zdt = ZonedDateTime.of(2026, 11, 6, 14, 30, 0, 0, defaultZone)
        val isoString = DateUtils.toIsoString(Date.from(zdt.toInstant()))
        assertEquals("06/11/2026", DateUtils.toDisplayDate(isoString))

        // String inválida com mais de 10 caracteres retorna substring(0, 10)
        assertEquals("2026-99-99", DateUtils.toDisplayDate("2026-99-99Invalid"))
    }

    @Test
    fun `toDisplayDateTime formats ISO string to dd-MM-yyyy HH-mm`() {
        assertEquals("-", DateUtils.toDisplayDateTime(null))
        assertEquals("-", DateUtils.toDisplayDateTime(""))

        val zdt = ZonedDateTime.of(2026, 11, 6, 14, 30, 0, 0, defaultZone)
        val isoString = DateUtils.toIsoString(Date.from(zdt.toInstant()))
        assertEquals("06/11/2026 14:30", DateUtils.toDisplayDateTime(isoString))
    }

    @Test
    fun `toDeliveryDisplay formats to Brazilian style 'dd-MM-yyyy as HH-mm'`() {
        assertEquals("-", DateUtils.toDeliveryDisplay(null))
        assertEquals("-", DateUtils.toDeliveryDisplay(""))

        val zdt = ZonedDateTime.of(2026, 11, 6, 14, 30, 0, 0, defaultZone)
        val isoString = DateUtils.toIsoString(Date.from(zdt.toInstant()))
        assertEquals("06/11/2026 às 14:30", DateUtils.toDeliveryDisplay(isoString))
    }

    @Test
    fun `formatFilterLabel formats dia, mes and ano correctly`() {
        val zdt = ZonedDateTime.of(2026, 11, 6, 10, 0, 0, 0, defaultZone)
        val date = Date.from(zdt.toInstant())

        assertEquals("06/11/2026", DateUtils.formatFilterLabel("dia", date))
        assertEquals("Nov./2026", DateUtils.formatFilterLabel("mes", date).replace("nov.", "Nov."))
        assertEquals("2026", DateUtils.formatFilterLabel("ano", date))
        assertEquals("", DateUtils.formatFilterLabel("unknown", date))
    }

    @Test
    fun `updateDate updates year, month and day preserving time`() {
        val original = ZonedDateTime.of(2026, 5, 10, 14, 30, 15, 0, defaultZone)
        val updated = DateUtils.updateDate(
            current = Date.from(original.toInstant()),
            year = 2027,
            month = 10, // Mês 10 no DatePicker representa Novembro (10 + 1 = 11)
            dayOfMonth = 25
        )

        val updatedZdt = updated.toInstant().atZone(defaultZone)
        assertEquals(2027, updatedZdt.year)
        assertEquals(11, updatedZdt.monthValue)
        assertEquals(25, updatedZdt.dayOfMonth)
        assertEquals(14, updatedZdt.hour)
        assertEquals(30, updatedZdt.minute)
    }

    @Test
    fun `updateTime updates hour and minute preserving date`() {
        val original = ZonedDateTime.of(2026, 11, 6, 10, 15, 0, 0, defaultZone)
        val updated = DateUtils.updateTime(
            current = Date.from(original.toInstant()),
            hourOfDay = 18,
            minute = 45
        )

        val updatedZdt = updated.toInstant().atZone(defaultZone)
        assertEquals(2026, updatedZdt.year)
        assertEquals(11, updatedZdt.monthValue)
        assertEquals(6, updatedZdt.dayOfMonth)
        assertEquals(18, updatedZdt.hour)
        assertEquals(45, updatedZdt.minute)
    }

    @Test
    fun `getDayRange returns start and end of day in query format`() {
        val zdt = ZonedDateTime.of(2026, 11, 6, 12, 0, 0, 0, defaultZone)
        val (start, end) = DateUtils.getDayRange(Date.from(zdt.toInstant()))

        assertEquals("2026-11-06T00:00:00", start)
        assertEquals("2026-11-06T23:59:59", end)
    }

    @Test
    fun `getMonthRange returns start and end of month in query format`() {
        val zdt = ZonedDateTime.of(2026, 2, 10, 12, 0, 0, 0, defaultZone)
        val (start, end) = DateUtils.getMonthRange(Date.from(zdt.toInstant()))

        assertEquals("2026-02-01T00:00:00", start)
        assertEquals("2026-02-28T23:59:59", end)
    }

    @Test
    fun `getYearRange returns start and end of year in query format`() {
        val zdt = ZonedDateTime.of(2026, 7, 15, 12, 0, 0, 0, defaultZone)
        val (start, end) = DateUtils.getYearRange(Date.from(zdt.toInstant()))

        assertEquals("2026-01-01T00:00:00", start)
        assertEquals("2026-12-31T23:59:59", end)
    }
}
