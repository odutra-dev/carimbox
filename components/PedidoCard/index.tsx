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
};

export const PedidoCard: React.FC<PedidoCardProps> = ({ pedido }) => {
  const styles = useStyles();
  const [isEntregue, setIsEntregue] = useState(pedido.entregue ?? false);

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
  } = pedido;

  const handleSendWhatsApp = () => {
    const mensagem = `Olá ${cliente}, aqui é da Carimbos Ocian! 😄

Segue o resumo do seu pedido:

🧾 Pedido nº: ${id}
🔖 Modelo: ${tipo_modelo}

✏ Descrição:
${texto_logo}

🔎 Observação: ${observacao}

💰 Forma de pagamento: ${tipo_pagamento}
💵 Valor total: R$ ${valor.toFixed(2)}
📅 Entrega Prevista: ${new Date(data_entrega).toLocaleString("pt-BR")}

🙏 Agradecemos pela preferência!

📦 Obs: Para retirar o pedido, basta informar o número do pedido enviado acima.
`;

    const numero = whatsapp.toString().replace(/\D/g, "");
    const url = `https://wa.me/${numero}?text=${encodeURIComponent(mensagem)}`;

    Linking.openURL(url).catch(() =>
      alert("Não foi possível abrir o WhatsApp.")
    );
  };

  const handleViewDetails = () => {
    router.push(`/verPedido/${id}`);
  };

  const handleCheck = () => {
    // Evita clicar novamente se já estiver concluído
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
            const { error } = await supabase
              .from("Pedidos")
              .update({ entregue: true })
              .eq("id", id)
              .select(); // garante retorno

            if (error) {
              Alert.alert("Erro", "Não foi possível atualizar o pedido.");
              console.error(error);
            } else {
              setIsEntregue(true); // atualiza o estado local
              ToastAndroid.show("Pedido concluído com sucesso!", 2000);
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
              <CircleCheckBig size={24} color={styles.iconCheck.color} />
            </TouchableOpacity>
          ) : null}

          <TouchableOpacity onPress={handleSendWhatsApp}>
            <ExternalLink size={24} color={styles.icon.color} />
          </TouchableOpacity>
        </View>
      </View>
    </TouchableOpacity>
  );
};
