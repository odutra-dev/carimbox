import React, { useCallback, useEffect, useState } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  Alert,
  ToastAndroid,
  ActivityIndicator,
} from "react-native";
import { useFocusEffect, useLocalSearchParams, useRouter } from "expo-router";
import * as Clipboard from "expo-clipboard";
import { supabase } from "../../utils/supabase";
import { Pedido } from "../../@types/Pedido";
import { useStyles } from "../../styles/verPedidoPage";
import { useTheme } from "../../hooks/useTheme";
import { InfoRow } from "../../components/InfoRow";

// Icons
import User from "lucide-react-native/dist/esm/icons/user";
import Phone from "lucide-react-native/dist/esm/icons/phone";
import Package from "lucide-react-native/dist/esm/icons/package";
import Type from "lucide-react-native/dist/esm/icons/type";
import Calendar from "lucide-react-native/dist/esm/icons/calendar";
import DollarSign from "lucide-react-native/dist/esm/icons/dollar-sign";
import CreditCard from "lucide-react-native/dist/esm/icons/credit-card";
import FileType from "lucide-react-native/dist/esm/icons/file-type";
import Pencil from "lucide-react-native/dist/esm/icons/pencil";
import MoveLeft from "lucide-react-native/dist/esm/icons/move-left";
import Copy from "lucide-react-native/dist/esm/icons/copy";
import CircleX from "lucide-react-native/dist/esm/icons/circle-x";
import { i18n } from "../../i18n";

export default function VerPedido() {
  const { id } = useLocalSearchParams();
  const router = useRouter();
  const { theme } = useTheme();
  const { colors } = theme;
  const styles = useStyles();

  const [pedido, setPedido] = useState<Pedido | null>(null);
  const [loading, setLoading] = useState(true);

  // 🔍 Busca o pedido específico no Supabase
  const getPedido = async () => {
    try {
      const { data, error } = await supabase
        .from("Pedidos")
        .select("*")
        .eq("id", Number(id))
        .single();

      if (error) throw error;
      setPedido(data);
    } catch (err: any) {
      console.error("Erro ao buscar pedido:", err.message);
      Alert.alert("Erro", "Não foi possível carregar o pedido.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (id) getPedido();
  }, [id]);

  useFocusEffect(
    useCallback(() => {
      getPedido();
    }, [])
  );

  const handleCopyAll = async () => {
    if (!pedido) return;

    const texto = `
Pedido #${pedido.id}
${pedido.cliente ? `Cliente: ${pedido.cliente}` : ""}
WhatsApp: ${pedido.whatsapp}
${pedido.tipo_modelo ? `Modelo: ${pedido.tipo_modelo}` : ""}
Texto/Logo: ${pedido.texto_logo}
Forma de Pagamento: ${pedido.tipo_pagamento}
Valor: R$ ${pedido.valor.toFixed(2)}
Data Pedido: ${new Date(pedido.data_pedido).toLocaleString("pt-BR")}
Entrega: ${new Date(pedido.data_entrega).toLocaleString("pt-BR")}
Observação: ${pedido.observacao || "Nenhuma"}
    `.trim();

    await Clipboard.setStringAsync(texto);

    ToastAndroid.show("Texto copiado para a área de transferência.", 2000);
  };

  const handleReOpenPedido = () => {
    Alert.alert(
      "Confirmar reabertura do Pedido?",
      `Deseja marcar o pedido #${id} como não concluído?`,
      [
        { text: "Cancelar", style: "cancel" },
        {
          text: "Confirmar",
          onPress: async () => {
            const { error } = await supabase
              .from("Pedidos")
              .update({ entregue: false })
              .eq("id", id);

            if (error) {
              Alert.alert("Erro", "Não foi possível atualizar o pedido.");
              console.error(error);
            } else {
              ToastAndroid.show("Pedido reaberto com sucesso!", 2000);
              getPedido();
            }
          },
        },
      ]
    );
  };

  const formatarWhatsapp = (numero: string) => {
    const num = numero.replace(/\D/g, "");

    if (num.length === 11) {
      return `(${num.slice(0, 2)}) ${num.slice(2, 7)}-${num.slice(7)}`;
    }

    if (num.length === 10) {
      return `(${num.slice(0, 2)}) ${num.slice(2, 6)}-${num.slice(6)}`;
    }

    return numero;
  };

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color={colors.primary} />
        <Text style={{ marginTop: 10, color: colors.textPrimary }}>
          Carregando pedido...
        </Text>
      </View>
    );
  }

  if (!pedido) {
    return (
      <View style={styles.center}>
        <Text style={{ color: colors.textPrimary }}>
          Pedido não encontrado.
        </Text>
        <TouchableOpacity
          style={styles.voltarBtn}
          onPress={() => router.back()}
        >
          <Pencil size={18} color={colors.primary} />
          <Text style={styles.voltarText}>Voltar</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={{ paddingBottom: 30 }}
    >
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()}>
          <MoveLeft size={24} color={colors.primary} />
        </TouchableOpacity>

        <Text style={styles.title}>
          {i18n.t("PedidoID")} #{pedido.id}
        </Text>

        <TouchableOpacity
          onPress={() => router.navigate(`/atualizarPedido/${id}`)}
        >
          <Pencil size={24} color={colors.primary} />
        </TouchableOpacity>

        {pedido.entregue && (
          <TouchableOpacity onPress={handleReOpenPedido}>
            <CircleX size={24} color={colors.error} />
          </TouchableOpacity>
        )}
      </View>

      <View style={styles.card}>
        {pedido.cliente?.length > 0 && (
          <InfoRow
            label={i18n.t("Cliente")}
            value={pedido.cliente}
            Icon={User}
          />
        )}
        <InfoRow
          label={i18n.t("WhatsApp")}
          value={formatarWhatsapp(String(pedido.whatsapp))}
          Icon={Phone}
        />
        {pedido.tipo_modelo?.length > 0 && (
          <InfoRow
            label={i18n.t("TipoModelo")}
            value={pedido.tipo_modelo}
            Icon={Package}
          />
        )}
        <InfoRow
          label={i18n.t("TextoLogo")}
          value={pedido.texto_logo}
          Icon={Type}
        />
        <InfoRow
          label={i18n.t("TipoPagamento")}
          value={pedido.tipo_pagamento}
          Icon={CreditCard}
        />
        <InfoRow
          label={i18n.t("Valor")}
          value={`R$ ${pedido.valor.toFixed(2)}`}
          Icon={DollarSign}
        />
        <InfoRow
          label={i18n.t("DataPedido")}
          value={new Date(pedido.data_pedido).toLocaleString("pt-BR")}
          Icon={Calendar}
        />
        <InfoRow
          label={i18n.t("DataEntrega")}
          value={new Date(pedido.data_entrega).toLocaleString("pt-BR")}
          Icon={Calendar}
        />

        {pedido.observacao ? (
          <InfoRow
            label={i18n.t("Observacao")}
            value={pedido.observacao}
            Icon={FileType}
          />
        ) : null}
      </View>

      <TouchableOpacity style={styles.copyButton} onPress={handleCopyAll}>
        <Copy size={18} color={colors.white} style={{ marginRight: 8 }} />
        <Text style={styles.copyButtonText}>{i18n.t("CopiarTodos")}</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}
