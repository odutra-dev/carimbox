import React from "react";
import { View, Text, TouchableOpacity, Linking } from "react-native";
import { Pedido } from "../../@types/Pedido";
import { router } from "expo-router";
import { useStyles } from "./styles";

import ExternalLink from "lucide-react-native/dist/esm/icons/external-link";
import Eye from "lucide-react-native/dist/esm/icons/eye";

type PedidoCardProps = {
  pedido: Pedido;
};

export const PedidoCard: React.FC<PedidoCardProps> = ({ pedido }) => {
  const styles = useStyles();
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
  } = pedido;

  const handleSendWhatsApp = () => {
    const mensagem = `Olá ${cliente}, aqui é da Carimbos Ocian!

Segue o resumo do seu pedido:

Pedido nº: ${id}

Modelo: ${tipo_modelo}
Logotipo/Textos: ${texto_logo}
Forma de pagamento: ${tipo_pagamento}
Data e horário de retirada: ${new Date(data_entrega).toLocaleString("pt-BR")}
Valor total: R$ ${valor.toFixed(2)}

Qualquer dúvida, estamos à disposição!`;

    const numero = whatsapp.toString().replace(/\D/g, "");
    const url = `https://wa.me/${numero}?text=${encodeURIComponent(mensagem)}`;

    Linking.openURL(url).catch(() =>
      alert("Não foi possível abrir o WhatsApp.")
    );
  };

  const handleViewDetails = () => {
    router.push(`/verPedido/${id}`);
  };

  return (
    <View style={styles.card}>
      <View style={styles.header}>
        <View>
          <Text style={styles.idText}>Pedido #{id}</Text>
          <Text style={styles.dataText}>
            {new Date(data_pedido).toLocaleString("pt-BR")}
          </Text>
          <Text style={styles.clienteText}>{cliente}</Text>
        </View>

        <View style={styles.iconContainer}>
          <TouchableOpacity onPress={handleViewDetails}>
            <Eye size={24} color={styles.icon.color} />
          </TouchableOpacity>
          <TouchableOpacity onPress={handleSendWhatsApp}>
            <ExternalLink size={24} color={styles.icon.color} />
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
};
