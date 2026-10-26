package com.dutradev.carimbosocian.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation(private val ddi: String) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val clean = text.text.filter { it.isDigit() }

        if (clean.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        return when (ddi) {
            "55" -> transformBrazil(clean)
            "1" -> transformUs(clean)
            "351" -> transformPortugal(clean)
            "34" -> transformSpain(clean)
            "54" -> transformArgentina(clean)
            else -> TransformedText(text, OffsetMapping.Identity)
        }
    }

    private fun transformBrazil(digits: String): TransformedText {
        val clean = digits.take(11)
        val out = StringBuilder()
        val origToTrans = IntArray(clean.length + 1)

        for (i in clean.indices) {
            if (i == 0) out.append('(')
            origToTrans[i] = out.length
            out.append(clean[i])
            if (i == 1) {
                out.append(") ")
            } else if (clean.length == 11 && i == 6) {
                out.append('-')
            } else if (clean.length <= 10 && clean.length > 6 && i == 5) {
                out.append('-')
            }
        }
        origToTrans[clean.length] = out.length

        val transToOrig = IntArray(out.length + 1)
        var o = 0
        for (t in 0..out.length) {
            while (o < clean.length && origToTrans[o + 1] <= t) {
                o++
            }
            transToOrig[t] = o
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return origToTrans[offset.coerceIn(0, clean.length)]
            }

            override fun transformedToOriginal(offset: Int): Int {
                return transToOrig[offset.coerceIn(0, out.length)]
            }
        }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }

    private fun transformUs(digits: String): TransformedText {
        val clean = digits.take(10)
        val out = StringBuilder()
        val origToTrans = IntArray(clean.length + 1)

        for (i in clean.indices) {
            if (i == 0) out.append('(')
            origToTrans[i] = out.length
            out.append(clean[i])
            if (i == 2) {
                out.append(") ")
            } else if (i == 5) {
                out.append('-')
            }
        }
        origToTrans[clean.length] = out.length

        val transToOrig = IntArray(out.length + 1)
        var o = 0
        for (t in 0..out.length) {
            while (o < clean.length && origToTrans[o + 1] <= t) {
                o++
            }
            transToOrig[t] = o
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return origToTrans[offset.coerceIn(0, clean.length)]
            }

            override fun transformedToOriginal(offset: Int): Int {
                return transToOrig[offset.coerceIn(0, out.length)]
            }
        }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }

    private fun transformPortugal(digits: String): TransformedText {
        val clean = digits.take(9)
        val out = StringBuilder()
        val origToTrans = IntArray(clean.length + 1)

        for (i in clean.indices) {
            origToTrans[i] = out.length
            out.append(clean[i])
            if (i == 2 || i == 5) {
                out.append(' ')
            }
        }
        origToTrans[clean.length] = out.length

        val transToOrig = IntArray(out.length + 1)
        var o = 0
        for (t in 0..out.length) {
            while (o < clean.length && origToTrans[o + 1] <= t) {
                o++
            }
            transToOrig[t] = o
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return origToTrans[offset.coerceIn(0, clean.length)]
            }

            override fun transformedToOriginal(offset: Int): Int {
                return transToOrig[offset.coerceIn(0, out.length)]
            }
        }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }

    private fun transformSpain(digits: String): TransformedText {
        val clean = digits.take(9)
        val out = StringBuilder()
        val origToTrans = IntArray(clean.length + 1)

        for (i in clean.indices) {
            origToTrans[i] = out.length
            out.append(clean[i])
            if (i == 2 || i == 4 || i == 6) {
                out.append(' ')
            }
        }
        origToTrans[clean.length] = out.length

        val transToOrig = IntArray(out.length + 1)
        var o = 0
        for (t in 0..out.length) {
            while (o < clean.length && origToTrans[o + 1] <= t) {
                o++
            }
            transToOrig[t] = o
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return origToTrans[offset.coerceIn(0, clean.length)]
            }

            override fun transformedToOriginal(offset: Int): Int {
                return transToOrig[offset.coerceIn(0, out.length)]
            }
        }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }

    private fun transformArgentina(digits: String): TransformedText {
        val clean = digits.take(11)
        val out = StringBuilder()
        val origToTrans = IntArray(clean.length + 1)

        for (i in clean.indices) {
            origToTrans[i] = out.length
            out.append(clean[i])
            if (i == 1) {
                out.append(' ')
            } else if (i == 5) {
                out.append('-')
            }
        }
        origToTrans[clean.length] = out.length

        val transToOrig = IntArray(out.length + 1)
        var o = 0
        for (t in 0..out.length) {
            while (o < clean.length && origToTrans[o + 1] <= t) {
                o++
            }
            transToOrig[t] = o
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return origToTrans[offset.coerceIn(0, clean.length)]
            }

            override fun transformedToOriginal(offset: Int): Int {
                return transToOrig[offset.coerceIn(0, out.length)]
            }
        }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }
}
