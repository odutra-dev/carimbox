import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  ScrollView,
  Alert,
  KeyboardAvoidingView,
  Platform,
  ActivityIndicator,
  ToastAndroid,
} from "react-native";
import DateTimePickerModal from "react-native-modal-datetime-picker";
import { supabase } from "../../utils/supabase";
import { router, useLocalSearchParams } from "expo-router";
import { Input } from "../../components/Input";
import { InputSelect } from "../../components/InputSelect";
import useStyles from "../../styles/novoPedidoPage";
import { useTheme } from "../../hooks/useTheme";
import MoveLeft from "lucide-react-native/dist/esm/icons/move-left";
import { i18n } from "../../i18n";
import { Picker } from "@react-native-picker/picker";

export default function AtualizarPedidoScreen() {
  const { theme } = useTheme();
  const { colors, spacing } = theme;
  const styles = useStyles();
  const { id } = useLocalSearchParams();

  const [loading, setLoading] = useState(false);
  const [carregandoPedido, setCarregandoPedido] = useState(true);
  const [isDatePickerVisible, setDatePickerVisibility] = useState(false);

  const [form, setForm] = useState({
    cliente: "",
    whatsapp: "",
    tipo_modelo: "",
    texto_logo: "",
    tipo_pagamento: "",
    valor: "",
    observacao: "",
    entregue: false,
    data_entrega: new Date(),
    label: "Carimbo",
  });

  const showDatePicker = () => setDatePickerVisibility(true);
  const hideDatePicker = () => setDatePickerVisibility(false);

  const handleConfirm = (date: Date) => {
    handleChange("data_entrega", date);
    hideDatePicker();
  };

  const handleChange = (key: string, value: string | Date) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  useEffect(() => {
    const fetchPedido = async () => {
      if (!id) return;
      try {
        const { data, error } = await supabase
          .from("Pedidos")
          .select("*")
          .eq("id", id)
          .single();

        if (error) throw error;

        setForm({
          cliente: data.cliente || "",
          whatsapp: data.whatsapp?.toString() || "",
          tipo_modelo: data.tipo_modelo || "",
          texto_logo: data.texto_logo || "",
          tipo_pagamento: data.tipo_pagamento || "",
          valor: data.valor?.toString() || "",
          observacao: data.observacao || "",
          entregue: data.entregue || false,
          data_entrega: data.data_entrega
            ? new Date(data.data_entrega)
            : new Date(),
          label: data.label || "Carimbo",
        });
      } catch (err: any) {
        Alert.alert(i18n.t("Erro"), err.message);
      } finally {
        setCarregandoPedido(false);
      }
    };
    fetchPedido();
  }, [id]);

  const handleSubmit = async () => {
    if (!form.whatsapp || !form.texto_logo || !form.valor) {
      Alert.alert(i18n.t("Erro"), i18n.t("AlertaCamposObrigatoriosAtualizar"));
      return;
    }

    setLoading(true);
    try {
      const { error } = await supabase
        .from("Pedidos")
        .update({
          cliente: form.cliente,
          whatsapp: Number(form.whatsapp),
          tipo_modelo: form.tipo_modelo,
          texto_logo: form.texto_logo,
          tipo_pagamento: form.tipo_pagamento,
          valor: Number(form.valor),
          observacao: form.observacao || null,
          entregue: form.entregue,
          data_entrega: form.data_entrega.toISOString(),
          label: form.label,
        })
        .eq("id", id);

      if (error) throw error;

      ToastAndroid.show(i18n.t("PedidoAtualizadoSucesso"), 2000);
      router.back();
    } catch (err: any) {
      Alert.alert(i18n.t("Erro"), err.message);
    } finally {
      setLoading(false);
    }
  };

  if (carregandoPedido) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color={colors.primary} />
        <Text style={{ color: colors.textPrimary, marginTop: 10 }}>
          {i18n.t("CarregandoPedido")}
        </Text>
      </View>
    );
  }

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === "ios" ? "padding" : "height"}
      style={{ flex: 1, backgroundColor: colors.background }}
    >
      <ScrollView
        contentContainerStyle={styles.container}
        keyboardShouldPersistTaps="handled"
      >
        <View style={{ flexDirection: "row", alignItems: "center" }}>
          <TouchableOpacity onPress={() => router.back()}>
            <MoveLeft size={24} color={colors.primary} />
          </TouchableOpacity>
          <Text style={styles.title}>{i18n.t("AtualizarPedidoTitulo")}</Text>
        </View>
        <Text style={styles.subtitle}>
          {i18n.t("AtualizarPedidoSubtitulo")}
        </Text>

        <Input
          label={i18n.t("ClienteAtualizarPedido")}
          placeholder={i18n.t("ClienteAtualizarPedidoPlaceholder")}
          value={form.cliente}
          onChangeText={(v) => handleChange("cliente", v)}
        />
        <Input
          label={i18n.t("WhatsAppAtualizarPedido")}
          placeholder={i18n.t("WhatsAppAtualizarPedidoPlaceholder")}
          keyboardType="numeric"
          value={form.whatsapp}
          onChangeText={(v) => handleChange("whatsapp", v)}
        />
        <Input
          label={i18n.t("TipoModeloAtualizarPedido")}
          placeholder={i18n.t("TipoModeloAtualizarPedidoPlaceholder")}
          value={form.tipo_modelo}
          onChangeText={(v) => handleChange("tipo_modelo", v)}
        />
        <Input
          label={i18n.t("TextoLogoAtualizarPedido")}
          placeholder={i18n.t("TextoLogoAtualizarPedidoPlaceholder")}
          value={form.texto_logo}
          onChangeText={(v) => handleChange("texto_logo", v)}
          multiline
        />
        <Input
          label={i18n.t("ObservacaoAtualizarPedido")}
          placeholder={i18n.t("ObservacaoAtualizarPedidoPlaceholder")}
          value={form.observacao}
          onChangeText={(v) => handleChange("observacao", v)}
          multiline
        />
        <InputSelect
          label={i18n.t("TipoPagamentoAtualizarPedido")}
          value={form.tipo_pagamento}
          onChange={(v) => handleChange("tipo_pagamento", v)}
        />
        <Input
          label={i18n.t("ValorAtualizarPedido")}
          placeholder={i18n.t("ValorAtualizarPedidoPlaceholder")}
          keyboardType="decimal-pad"
          value={form.valor}
          onChangeText={(v) => handleChange("valor", v)}
        />

        <View style={styles.dateSection}>
          <Text style={styles.label}>
            {i18n.t("DataEntregaAtualizarPedido")}
          </Text>
          <TouchableOpacity style={styles.dateButton} onPress={showDatePicker}>
            <Text style={styles.dateText}>
              {form.data_entrega.toLocaleDateString("pt-BR")}{" "}
              {form.data_entrega.toLocaleTimeString("pt-BR", {
                hour: "2-digit",
                minute: "2-digit",
              })}
            </Text>
          </TouchableOpacity>
        </View>

        <DateTimePickerModal
          isVisible={isDatePickerVisible}
          mode="datetime"
          date={form.data_entrega}
          onConfirm={handleConfirm}
          onCancel={hideDatePicker}
          locale="pt-BR"
          is24Hour
        />

        <Text style={styles.label}>{i18n.t("Label")}</Text>
        <View
          style={{
            backgroundColor: colors.surface,
            borderRadius: spacing.sm,
            borderWidth: 1,
            borderColor: colors.border,
            overflow: "hidden",
          }}
        >
          <Picker
            selectedValue={form.label}
            onValueChange={(v) => handleChange("label", v)}
            style={{
              height: 50,
              color: colors.textPrimary,
              paddingHorizontal: spacing.md,
            }}
          >
            <Picker.Item label={i18n.t("labelCarimbo")} value="Carimbo" />
            <Picker.Item label={i18n.t("labelMdf")} value="Mdf/Acrílico" />
            <Picker.Item label={i18n.t("labelOuthers")} value="Outros" />
          </Picker>
        </View>

        <TouchableOpacity
          style={[styles.button, loading && { opacity: 0.6 }]}
          onPress={handleSubmit}
          disabled={loading}
        >
          <Text style={styles.buttonText}>
            {loading ? i18n.t("Salvando") : i18n.t("AtualizarPedido")}
          </Text>
        </TouchableOpacity>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
