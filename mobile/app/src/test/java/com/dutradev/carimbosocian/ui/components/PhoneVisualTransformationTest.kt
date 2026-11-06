package com.dutradev.carimbosocian.ui.components

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneVisualTransformationTest {

    @Test
    fun `filter returns identity when text is empty`() {
        val transformation = PhoneVisualTransformation(ddi = "55")
        val result = transformation.filter(AnnotatedString(""))

        assertEquals("", result.text.text)
        assertEquals(0, result.offsetMapping.originalToTransformed(0))
        assertEquals(0, result.offsetMapping.transformedToOriginal(0))
    }

    @Test
    fun `filter formats brazilian 11 digits mobile phone and maps offsets correctly`() {
        val transformation = PhoneVisualTransformation(ddi = "55")
        val input = "13999998888"
        val result = transformation.filter(AnnotatedString(input))

        assertEquals("(13) 99999-8888", result.text.text)

        // Offset mapping verification
        val mapping = result.offsetMapping
        // original 0 ('1') -> transformed 1 (after '(')
        assertEquals(1, mapping.originalToTransformed(0))
        // original 2 ('9') -> transformed 5 (after "(13) ")
        assertEquals(5, mapping.originalToTransformed(2))
        // original end (11) -> transformed end (15)
        assertEquals(15, mapping.originalToTransformed(11))

        // transformed to original mapping
        assertEquals(0, mapping.transformedToOriginal(0))
        assertEquals(0, mapping.transformedToOriginal(1))
        assertEquals(11, mapping.transformedToOriginal(15))
    }

    @Test
    fun `filter formats brazilian 10 digits landline phone`() {
        val transformation = PhoneVisualTransformation(ddi = "55")
        val input = "1334912345"
        val result = transformation.filter(AnnotatedString(input))

        assertEquals("(13) 3491-2345", result.text.text)
    }

    @Test
    fun `filter formats US phone correctly`() {
        val transformation = PhoneVisualTransformation(ddi = "1")
        val input = "2025550123"
        val result = transformation.filter(AnnotatedString(input))

        assertEquals("(202) 555-0123", result.text.text)

        val mapping = result.offsetMapping
        assertEquals(1, mapping.originalToTransformed(0))
        assertEquals(14, mapping.originalToTransformed(10))
    }

    @Test
    fun `filter formats Portugal phone correctly`() {
        val transformation = PhoneVisualTransformation(ddi = "351")
        val input = "912345678"
        val result = transformation.filter(AnnotatedString(input))

        assertEquals("912 345 678", result.text.text)
    }

    @Test
    fun `filter formats Spain phone correctly`() {
        val transformation = PhoneVisualTransformation(ddi = "34")
        val input = "612345678"
        val result = transformation.filter(AnnotatedString(input))

        assertEquals("612 34 56 78", result.text.text)
    }

    @Test
    fun `filter formats Argentina phone correctly`() {
        val transformation = PhoneVisualTransformation(ddi = "54")
        val input = "1123456789"
        val result = transformation.filter(AnnotatedString(input))

        assertEquals("11 2345-6789", result.text.text)
    }

    @Test
    fun `filter leaves unknown DDI unmasked`() {
        val transformation = PhoneVisualTransformation(ddi = "999")
        val input = "12345678"
        val result = transformation.filter(AnnotatedString(input))

        assertEquals("12345678", result.text.text)
    }
}
