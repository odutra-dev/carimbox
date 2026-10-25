package com.dutradev.carimbosocian.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.text.NumberFormat
import java.util.Locale

object IntentUtils {

    fun openWhatsApp(context: Context, rawPhone: Long) {
        val phoneString = rawPhone.toString().filter { it.isDigit() }
        // Se não tiver DDI (ex: 55), adiciona 55 caso tenha 10 ou 11 dígitos
        val formattedNumber = if (phoneString.length in 10..11) {
            "55$phoneString"
        } else {
            phoneString
        }

        try {
            val url = "https://api.whatsapp.com/send?phone=$formattedNumber"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Não foi possível abrir o WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyToClipboard(context: Context, label: String, text: String, feedbackMessage: String? = null) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard?.setPrimaryClip(clip)

        feedbackMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    fun formatCurrency(value: Double): String {
        return try {
            val ptBr = Locale("pt", "BR")
            val formatter = NumberFormat.getCurrencyInstance(ptBr)
            formatter.format(value)
        } catch (_: Exception) {
            "R$ %.2f".format(value)
        }
    }
}
