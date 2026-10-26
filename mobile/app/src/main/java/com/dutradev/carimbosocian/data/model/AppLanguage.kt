package com.dutradev.carimbosocian.data.model

import com.dutradev.carimbosocian.R

enum class AppLanguage(val code: String, val titleResId: Int) {
    SYSTEM("", R.string.idioma_sistema),
    PORTUGUESE("pt", R.string.idioma_pt),
    ENGLISH("en", R.string.idioma_en),
    SPANISH("es", R.string.idioma_es);

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: SYSTEM
        }
    }
}
