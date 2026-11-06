package com.dutradev.carimbosocian.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneUtilsTest {

    @Test
    fun `isBrazilianFormat validates 11 digits mobile phones with valid DDD and starting 9`() {
        assertTrue(PhoneUtils.isBrazilianFormat("13999998888"))
        assertTrue(PhoneUtils.isBrazilianFormat("11987654321"))
        assertTrue(PhoneUtils.isBrazilianFormat("21912345678"))

        // DDD inválido
        assertFalse(PhoneUtils.isBrazilianFormat("00999998888"))
        assertFalse(PhoneUtils.isBrazilianFormat("20999998888"))

        // Não começa com 9 no 3º dígito
        assertFalse(PhoneUtils.isBrazilianFormat("13899998888"))
        assertFalse(PhoneUtils.isBrazilianFormat("11799998888"))
    }

    @Test
    fun `isBrazilianFormat validates 10 digits landline phones with valid DDD and starting 2 to 5`() {
        assertTrue(PhoneUtils.isBrazilianFormat("1334912345"))
        assertTrue(PhoneUtils.isBrazilianFormat("1122334455"))
        assertTrue(PhoneUtils.isBrazilianFormat("2155554444"))

        // Terceiro dígito fora de 2..5
        assertFalse(PhoneUtils.isBrazilianFormat("1314912345"))
        assertFalse(PhoneUtils.isBrazilianFormat("1364912345"))
        assertFalse(PhoneUtils.isBrazilianFormat("1394912345"))

        // Comprimento inválido
        assertFalse(PhoneUtils.isBrazilianFormat("139999888")) // 9 dígitos
        assertFalse(PhoneUtils.isBrazilianFormat("5513999998888")) // 13 dígitos
    }

    @Test
    fun `formatBrazilianPhone applies correct masks`() {
        assertEquals("", PhoneUtils.formatBrazilianPhone(""))
        assertEquals("(13", PhoneUtils.formatBrazilianPhone("13"))
        assertEquals("(13) 99", PhoneUtils.formatBrazilianPhone("1399"))
        assertEquals("(13) 3491-2345", PhoneUtils.formatBrazilianPhone("1334912345"))
        assertEquals("(13) 99999-8888", PhoneUtils.formatBrazilianPhone("13999998888"))
        assertEquals("(13) 99999-8888", PhoneUtils.formatBrazilianPhone("13999998888extraDigitsIgnored"))
    }

    @Test
    fun `formatUsPhone applies standard US mask`() {
        assertEquals("", PhoneUtils.formatUsPhone(""))
        assertEquals("(202", PhoneUtils.formatUsPhone("202"))
        assertEquals("(202) 555", PhoneUtils.formatUsPhone("202555"))
        assertEquals("(202) 555-0123", PhoneUtils.formatUsPhone("2025550123"))
    }

    @Test
    fun `formatPortugalPhone applies standard Portugal mask`() {
        assertEquals("", PhoneUtils.formatPortugalPhone(""))
        assertEquals("912", PhoneUtils.formatPortugalPhone("912"))
        assertEquals("912 345", PhoneUtils.formatPortugalPhone("912345"))
        assertEquals("912 345 678", PhoneUtils.formatPortugalPhone("912345678"))
    }

    @Test
    fun `formatSpainPhone applies standard Spain mask`() {
        assertEquals("", PhoneUtils.formatSpainPhone(""))
        assertEquals("612", PhoneUtils.formatSpainPhone("612"))
        assertEquals("612 34", PhoneUtils.formatSpainPhone("61234"))
        assertEquals("612 34 56", PhoneUtils.formatSpainPhone("6123456"))
        assertEquals("612 34 56 78", PhoneUtils.formatSpainPhone("612345678"))
    }

    @Test
    fun `formatArgentinaPhone applies standard Argentina mask`() {
        assertEquals("", PhoneUtils.formatArgentinaPhone(""))
        assertEquals("11", PhoneUtils.formatArgentinaPhone("11"))
        assertEquals("11 2345", PhoneUtils.formatArgentinaPhone("112345"))
        assertEquals("11 2345-6789", PhoneUtils.formatArgentinaPhone("1123456789"))
    }

    @Test
    fun `formatInputForDdi delegates to correct country formatter`() {
        assertEquals("(13) 99999-8888", PhoneUtils.formatInputForDdi("55", "13999998888"))
        assertEquals("(202) 555-0123", PhoneUtils.formatInputForDdi("1", "2025550123"))
        assertEquals("912 345 678", PhoneUtils.formatInputForDdi("351", "912345678"))
        assertEquals("612 34 56 78", PhoneUtils.formatInputForDdi("34", "612345678"))
        assertEquals("11 2345-6789", PhoneUtils.formatInputForDdi("54", "1123456789"))
        assertEquals("1234 5678", PhoneUtils.formatInputForDdi("99", "12345678"))
    }

    @Test
    fun `formatPhoneDisplay formats numbers according to country code and length`() {
        assertEquals("", PhoneUtils.formatPhoneDisplay(0L))
        assertEquals("(13) 99999-8888", PhoneUtils.formatPhoneDisplay(13999998888L))
        assertEquals("(13) 3491-2345", PhoneUtils.formatPhoneDisplay(1334912345L))
        assertEquals("+55 (13) 99999-8888", PhoneUtils.formatPhoneDisplay(5513999998888L))
        assertEquals("+55 (13) 3491-2345", PhoneUtils.formatPhoneDisplay(551334912345L))
        assertEquals("+1 (202) 555-0123", PhoneUtils.formatPhoneDisplay(12025550123L))
        assertEquals("+351 912 345 678", PhoneUtils.formatPhoneDisplay(351912345678L))
        assertEquals("+34 612 34 56 78", PhoneUtils.formatPhoneDisplay(34612345678L))
    }

    @Test
    fun `getWhatsAppTargetPhone ensures proper country code prefix`() {
        assertEquals("", PhoneUtils.getWhatsAppTargetPhone(0L))
        assertEquals("5513999998888", PhoneUtils.getWhatsAppTargetPhone(13999998888L))
        assertEquals("5513999998888", PhoneUtils.getWhatsAppTargetPhone(5513999998888L))
        assertEquals("12025550123", PhoneUtils.getWhatsAppTargetPhone(12025550123L))
        assertEquals("351912345678", PhoneUtils.getWhatsAppTargetPhone(351912345678L))
    }

    @Test
    fun `parseStoredPhone splits raw long into DDI and local number`() {
        assertEquals(Pair("55", ""), PhoneUtils.parseStoredPhone(0L))
        assertEquals(Pair("55", "13999998888"), PhoneUtils.parseStoredPhone(5513999998888L))
        assertEquals(Pair("55", "13999998888"), PhoneUtils.parseStoredPhone(13999998888L))
        assertEquals(Pair("1", "2025550123"), PhoneUtils.parseStoredPhone(12025550123L))
        assertEquals(Pair("351", "912345678"), PhoneUtils.parseStoredPhone(351912345678L))
        assertEquals(Pair("34", "612345678"), PhoneUtils.parseStoredPhone(34612345678L))
    }

    @Test
    fun `combineDdiAndNumber combines country code and local number appropriately`() {
        assertEquals(13999998888L, PhoneUtils.combineDdiAndNumber("55", "13999998888"))
        assertEquals(12025550123L, PhoneUtils.combineDdiAndNumber("1", "2025550123"))
        assertEquals(351912345678L, PhoneUtils.combineDdiAndNumber("351", "912345678"))
        assertEquals(0L, PhoneUtils.combineDdiAndNumber("55", ""))
    }

    @Test
    fun `isValidPhone checks validity according to DDI rules`() {
        // Brasil
        assertTrue(PhoneUtils.isValidPhone("55", "13999998888"))
        assertTrue(PhoneUtils.isValidPhone("55", "1334912345"))
        assertFalse(PhoneUtils.isValidPhone("55", "13899998888"))
        assertFalse(PhoneUtils.isValidPhone("55", "999998888"))

        // EUA (10 dígitos)
        assertTrue(PhoneUtils.isValidPhone("1", "2025550123"))
        assertFalse(PhoneUtils.isValidPhone("1", "202555012"))

        // Portugal (9 dígitos)
        assertTrue(PhoneUtils.isValidPhone("351", "912345678"))
        assertFalse(PhoneUtils.isValidPhone("351", "91234567"))

        // Espanha (9 dígitos)
        assertTrue(PhoneUtils.isValidPhone("34", "612345678"))
        assertFalse(PhoneUtils.isValidPhone("34", "61234567"))

        // Argentina (10 ou 11 dígitos)
        assertTrue(PhoneUtils.isValidPhone("54", "1123456789"))
        assertTrue(PhoneUtils.isValidPhone("54", "91123456789"))
        assertFalse(PhoneUtils.isValidPhone("54", "112345678"))
    }
}
