import React, { useState, useEffect, useRef } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  Linking,
  Alert,
  ToastAndroid,
  Animated,
  StyleSheet,
} from "react-native";
import { Pedido } from "../../@types/Pedido";
import { router } from "expo-router";
import { useStyles } from "./styles";
import { supabase } from "../../utils/supabase";
import ExternalLink from "lucide-react-native/dist/esm/icons/external-link";
import CircleCheckBig from "lucide-react-native/dist/esm/icons/circle-check-big";
import { i18n } from "../../i18n";

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
  } = pedido;

  // MENSAGEM NOVO PEDIDO

  // `Olá ${
  //  cliente ? `${cliente},` : ""
  //} aqui é da Carimbos Ocian! 😄`

  const mensagemNovoPedido = `
Segue o resumo do seu pedido:

━━━━━━━━━━━━━━━━━
📦 Nº DO PEDIDO
     *Nº: ${pedido.id}*
━━━━━━━━━━━━━━━━━

${tipo_modelo ? `🔖 Modelo: ${tipo_modelo}` : ""}

✏ Descrição:

${texto_logo}

${observacao ? `📝 Observação: ${observacao}` : ""}

💰 Forma de pagamento: ${tipo_pagamento}
💵 Valor total: R$ ${valor.toFixed(2).replace(".", ",")}

━━━━━━━━━━━━━━━━━
📦 ENTREGA PREVISTA
📅 ${new Date(data_entrega)
    .toLocaleString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    })
    .replace(",", " às")}
━━━━━━━━━━━━━━━━━

🙏 Agradecemos pela preferência!

📦 Obs: Para retirar o pedido, basta informar o número do pedido enviado acima.
`;

  // MENSAGEM DE PEDIDO CONCLUÍDO
  const mensagemPedidoConcluido = `✅ Pedido finalizado! ✅

📦 Seu pedido está pronto para retirada/entrega.
Qualquer dúvida, estamos aqui!

Obrigado pela confiança! 🙌🏽

Se puder, avalie a Carimbos Ocian no Google:
👉 https://g.page/r/CbOE8NI63dFnEBM/review

💙 Agradecemos pela confiança! 💙
`;

  // ENVIAR WHATSAPP
  const handleSendWhatsApp = (mensagem: string) => {
    const numero = whatsapp.toString().replace(/\D/g, "");
    const url = `https://wa.me/55${numero}?text=${encodeURIComponent(
      mensagem
    )}`;

    Linking.openURL(url).catch(() =>
      Alert.alert("Erro", "Não foi possível abrir o WhatsApp.")
    );
  };

  // IR PARA DETALHES
  const handleViewDetails = () => {
    router.push(`/verPedido/${id}`);
  };

  // MARCAR COMO CONCLUÍDO
  const handleCheck = () => {
    if (isEntregue) {
      ToastAndroid.show("Este pedido já está concluído!", 2000);
      return;
    }

    Alert.alert(
      i18n.t("MarcarConcluidoTitulo"),
      `${i18n.t("MarcarConcluidoMensagemP1")} #${id} ${i18n.t(
        "MarcarConcluidoMensagemP2"
      )}`,
      [
        { text: i18n.t("MarcarConcluidoBotaoCancelar"), style: "cancel" },
        {
          text: i18n.t("MarcarConcluidoBotaoConfirmar"),
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
              setIsEntregue(true);
              if (data) onUpdate?.(data);
              ToastAndroid.show(
                i18n.t("MarcarConcluidoMensagemAtualizado"),
                2000
              );
              handleSendWhatsApp(mensagemPedidoConcluido);
            }
          },
        },
      ]
    );
  };

  // FORMATAR TELEFONE
  const formatarWhatsapp = (numero: string) => {
    const num = numero.replace(/\D/g, "");

    if (num.length === 11)
      return `(${num.slice(0, 2)}) ${num.slice(2, 7)}-${num.slice(7)}`;

    if (num.length === 10)
      return `(${num.slice(0, 2)}) ${num.slice(2, 6)}-${num.slice(6)}`;

    return numero;
  };

  return (
    <TouchableOpacity
      onPress={handleViewDetails}
      style={styles.card}
      activeOpacity={0.8}
    >
      <View style={styles.header}>
        <View>
          <Text style={styles.idText}>
            {i18n.t("PedidoIDCard")} #{id}
          </Text>
          <Text style={styles.dataText}>
            {new Date(data_pedido).toLocaleString("pt-BR")}
          </Text>
          <Text style={styles.clienteText}>
            {formatarWhatsapp(String(whatsapp))}
          </Text>
        </View>

        <View style={styles.iconContainer}>
          {!isEntregue && (
            <TouchableOpacity onPress={handleCheck}>
              <CircleCheckBig size={45} color={styles.iconCheck.color} />
            </TouchableOpacity>
          )}

          <TouchableOpacity
            onPress={() => handleSendWhatsApp(mensagemNovoPedido)}
          >
            <ExternalLink size={45} color={styles.icon.color} />
          </TouchableOpacity>
        </View>
      </View>
    </TouchableOpacity>
  );
};

// SKELETON
export const PedidoCardSkeleton = () => {
  const styles = useStyles();

  const pulse = useRef(new Animated.Value(0.5)).current;

  useEffect(() => {
    Animated.loop(
      Animated.sequence([
        Animated.timing(pulse, {
          toValue: 1,
          duration: 700,
          useNativeDriver: true,
        }),
        Animated.timing(pulse, {
          toValue: 0.5,
          duration: 700,
          useNativeDriver: true,
        }),
      ])
    ).start();
  }, []);

  const Placeholder = ({ style }: any) => (
    <Animated.View
      style={[
        styles.skelPlaceholder,
        style,
        {
          opacity: pulse,
        },
      ]}
    />
  );

  return (
    <View style={styles.card}>
      <View style={styles.header}>
        <View>
          {/* Pedido #ID */}
          <Placeholder style={{ width: 130, height: 16 }} />

          {/* Data */}
          <Placeholder style={{ width: 160, height: 14, marginTop: 8 }} />

          {/* WhatsApp */}
          <Placeholder style={{ width: 150, height: 14, marginTop: 10 }} />
        </View>

        <View style={styles.iconContainer}>
          <Placeholder style={{ width: 32, height: 32, borderRadius: 16 }} />
          <Placeholder style={{ width: 32, height: 32, borderRadius: 16 }} />
        </View>
      </View>
    </View>
  );
};
