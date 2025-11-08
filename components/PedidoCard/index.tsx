import React, { useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  Linking,
  Alert,
  ToastAndroid,
} from "react-native";
import { Pedido } from "../../@types/Pedido";
import { router } from "expo-router";
import { useStyles } from "./styles";
import { supabase } from "../../utils/supabase";
import ExternalLink from "lucide-react-native/dist/esm/icons/external-link";
import CircleCheckBig from "lucide-react-native/dist/esm/icons/circle-check-big";

type PedidoCardProps = {
  pedido: Pedido;
  onUpdate?: (pedidoAtualizado: Pedido) => void;
};

export const PedidoCard: React.FC<PedidoCardProps> = ({ pedido, onUpdate }) => {
  const styles = useStyles();
  const [isEntregue, setIsEntregue] = useState(pedido.entregue);

  const {
    id,
    data_pedido,
    cliente,
    whatsapp,
    tipo_modelo,
    texto_logo,
    tipo_pagamento,
    valor,
    data_entrega,
    observacao,
    entregue,
  } = pedido;

  const mensagemNovoPedido = `Olá ${cliente}, aqui é da Carimbos Ocian! 😄

Segue o resumo do seu pedido:

🧾 Pedido nº: ${id}
🔖 Modelo: ${tipo_modelo}

✏ Descrição:
${texto_logo}

${observacao ? `📝 Observação: ${observacao}` : ""}

💰 Forma de pagamento: ${tipo_pagamento}
💵 Valor total: R$ ${valor.toFixed(2)}
📅 Entrega Prevista: ${new Date(data_entrega)
    .toLocaleString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    })
    .replace(",", " às")}

🙏 Agradecemos pela preferência!

📦 Obs: Para retirar o pedido, basta informar o número do pedido enviado acima.
`;

  const mensagemPedidoConcluido = `✅ Pedido finalizado! ✅

📦 Seu pedido está pronto para retirada/entrega.
Qualquer dúvida, estamos aqui!

Obrigado pela confiança! 🙌🏽

Se puder, avalie a Carimbos Ocian no Google:
👉 https://g.page/r/CbOE8NI63dFnEBM/review

💙 Agradecemos pela confiança! 💙
`;

  const handleSendWhatsApp = (mensagem: string) => {
    const numero = whatsapp.toString().replace(/\D/g, "");
    const url = `https://wa.me/55${numero}?text=${encodeURIComponent(
      mensagem
    )}`;

    Linking.openURL(url).catch(() =>
      alert("Não foi possível abrir o WhatsApp.")
    );
  };

  const handleViewDetails = () => {
    router.push(`/verPedido/${id}`);
  };

  const handleCheck = () => {
    if (isEntregue) {
      ToastAndroid.show("Este pedido já está concluído!", 2000);
      return;
    }

    Alert.alert(
      "Confirmar Conclusão?",
      `Deseja marcar o pedido #${id} como concluído?`,
      [
        { text: "Cancelar", style: "cancel" },
        {
          text: "Confirmar",
          onPress: async () => {
            const { data, error } = await supabase
              .from("Pedidos")
              .update({ entregue: true })
              .eq("id", id)
              .select()
              .single();

            if (error) {
              Alert.alert("Erro", "Não foi possível atualizar o pedido.");
              console.error(error);
            } else {
              setIsEntregue(true); // atualiza local
              if (data) onUpdate?.(data); // ✅ só chama se data existir
              ToastAndroid.show("Pedido concluído com sucesso!", 2000);
              handleSendWhatsApp(mensagemPedidoConcluido);
            }
          },
        },
      ]
    );
  };

  return (
    <TouchableOpacity
      onPress={handleViewDetails}
      style={styles.card}
      activeOpacity={0.8}
    >
      <View style={styles.header}>
        <View>
          <Text style={styles.idText}>Pedido #{id}</Text>
          <Text style={styles.dataText}>
            {new Date(data_pedido).toLocaleString("pt-BR")}
          </Text>
          <Text style={styles.clienteText}>{cliente}</Text>
        </View>

        <View style={styles.iconContainer}>
          {!isEntregue ? (
            <TouchableOpacity onPress={handleCheck}>
              <CircleCheckBig size={32} color={styles.iconCheck.color} />
            </TouchableOpacity>
          ) : null}

          <TouchableOpacity
            onPress={() => handleSendWhatsApp(mensagemNovoPedido)}
          >
            <ExternalLink size={32} color={styles.icon.color} />
          </TouchableOpacity>
        </View>
      </View>
    </TouchableOpacity>
  );
};
