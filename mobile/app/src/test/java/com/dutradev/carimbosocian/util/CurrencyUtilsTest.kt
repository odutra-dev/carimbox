package com.dutradev.carimbosocian.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencyUtilsTest {

    private fun normalizeCurrency(str: String): String {
        // Normaliza espaço não separável (NBSP \u00A0 ou \u202F) para espaço padrão
        return str.replace('\u00A0', ' ').replace('\u202F', ' ').trim()
    }

    @Test
    fun `formatCentsToCurrency formats zero and empty strings properly`() {
        assertEquals("R$ 0,00", normalizeCurrency(CurrencyUtils.formatCentsToCurrency("0")))
        assertEquals("R$ 0,00", normalizeCurrency(CurrencyUtils.formatCentsToCurrency("")))
        assertEquals("R$ 0,00", normalizeCurrency(CurrencyUtils.formatCentsToCurrency("abc")))
    }

    @Test
    fun `formatCentsToCurrency formats fractions and whole numbers correctly`() {
        assertEquals("R$ 0,01", normalizeCurrency(CurrencyUtils.formatCentsToCurrency("1")))
        assertEquals("R$ 0,10", normalizeCurrency(CurrencyUtils.formatCentsToCurrency("10")))
        assertEquals("R$ 1,00", normalizeCurrency(CurrencyUtils.formatCentsToCurrency("100")))
        assertEquals("R$ 45,00", normalizeCurrency(CurrencyUtils.formatCentsToCurrency("4500")))
        assertEquals("R$ 1.234,56", normalizeCurrency(CurrencyUtils.formatCentsToCurrency("123456")))
    }

    @Test
    fun `formatCentsToCurrency ignores non digit characters`() {
        assertEquals("R$ 45,00", normalizeCurrency(CurrencyUtils.formatCentsToCurrency("R$ 45,00")))
        assertEquals("R$ 99,90", normalizeCurrency(CurrencyUtils.formatCentsToCurrency("abc9990xyz")))
    }

    @Test
    fun `centsToDouble converts string cents into double value`() {
        assertEquals(0.0, CurrencyUtils.centsToDouble("0"), 0.0001)
        assertEquals(0.0, CurrencyUtils.centsToDouble(""), 0.0001)
        assertEquals(0.01, CurrencyUtils.centsToDouble("1"), 0.0001)
        assertEquals(0.5, CurrencyUtils.centsToDouble("50"), 0.0001)
        assertEquals(45.0, CurrencyUtils.centsToDouble("4500"), 0.0001)
        assertEquals(1234.56, CurrencyUtils.centsToDouble("123456"), 0.0001)
        assertEquals(45.0, CurrencyUtils.centsToDouble("abc4500"), 0.0001)
    }

    @Test
    fun `doubleToCentsDigits converts double into cents string`() {
        assertEquals("", CurrencyUtils.doubleToCentsDigits(0.0))
        assertEquals("", CurrencyUtils.doubleToCentsDigits(-10.0))
        assertEquals("1", CurrencyUtils.doubleToCentsDigits(0.01))
        assertEquals("50", CurrencyUtils.doubleToCentsDigits(0.50))
        assertEquals("4500", CurrencyUtils.doubleToCentsDigits(45.0))
        assertEquals("12345", CurrencyUtils.doubleToCentsDigits(123.45))
    }

    @Test
    fun `formatDoubleToCurrency formats double to localized Brazilian real currency`() {
        val formattedZero = normalizeCurrency(CurrencyUtils.formatDoubleToCurrency(0.0))
        assertTrue(formattedZero.contains("R$") && formattedZero.contains("0,00"))

        val formattedAmount = normalizeCurrency(CurrencyUtils.formatDoubleToCurrency(45.0))
        assertTrue(formattedAmount.contains("R$") && formattedAmount.contains("45,00"))

        val formattedThousands = normalizeCurrency(CurrencyUtils.formatDoubleToCurrency(1234.56))
        assertTrue(formattedThousands.contains("R$") && formattedThousands.contains("1.234,56"))
    }
}
