package com.dutradev.carimbosocian.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.dutradev.carimbosocian.data.model.Pedido
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.Locale

object IntentUtils {

    fun openWhatsApp(context: Context, rawPhone: Long, message: String? = null) {
        val formattedNumber = PhoneUtils.getWhatsAppTargetPhone(rawPhone)
        if (formattedNumber.isBlank()) {
            Toast.makeText(context, "Telefone inválido para WhatsApp", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val url = if (!message.isNullOrBlank()) {
                val encodedMessage = URLEncoder.encode(message, "UTF-8")
                "https://api.whatsapp.com/send?phone=$formattedNumber&text=$encodedMessage"
            } else {
                "https://api.whatsapp.com/send?phone=$formattedNumber"
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Não foi possível abrir o WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }

    fun buildMensagemNovoPedido(pedido: Pedido): String {
        val modeloParte = if (pedido.tipoModelo.isNotBlank()) "🔖 Modelo: ${pedido.tipoModelo}\n\n" else ""
        val observacaoParte = if (!pedido.observacao.isNullOrBlank()) "\n\n📝 Observação: ${pedido.observacao}" else ""
        val formattedValor = String.format(Locale("pt", "BR"), "%.2f", pedido.valor)
        val formattedDataEntrega = DateUtils.toDeliveryDisplay(pedido.dataEntrega)

        return """
Segue o resumo do seu pedido:

━━━━━━━━━━━━━━━━━
📦 Nº DO PEDIDO
     *Nº: ${pedido.id ?: 0}*
━━━━━━━━━━━━━━━━━

${modeloParte}✏ Descrição:

${pedido.textoLogo}$observacaoParte

💰 Forma de pagamento: ${pedido.tipoPagamento}
💵 Valor total: R$ $formattedValor

━━━━━━━━━━━━━━━━━
📦 RETIRADA A PARTIR DE
📅 $formattedDataEntrega
━━━━━━━━━━━━━━━━━

📍 *RETIRADA: DENTRO do CHAVEIRO OCIAN*
Rua Remo Estéfane, 50 – Ocian
        """.trimIndent()
    }

    fun buildMensagemPedidoConcluido(pedido: Pedido): String {
        return """
✅ Seu pedido já foi finalizado e está disponível para retirada! ✅

━━━━━━━━━━━━━━━━━━━━━━━━
📦 Retirada com o nº do pedido: ${pedido.id ?: 0}
━━━━━━━━━━━━━━━━━━━━━━━━

📍 *RETIRADA: DENTRO do CHAVEIRO OCIAN*
Rua Remo Estéfane, 50 – Ocian
https://maps.app.goo.gl/3FzpDGgtgv9ipG1f9

💙 Agradecemos pela confiança! 💙
        """.trimIndent()
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
            "R$ %.2f".format(value).replace(".", ",")
        }
    }
}
