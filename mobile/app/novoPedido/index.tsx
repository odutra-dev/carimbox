import React, { useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  ScrollView,
  Alert,
  KeyboardAvoidingView,
  Platform,
  ToastAndroid,
} from "react-native";
import DateTimePickerModal from "react-native-modal-datetime-picker";
import { supabase } from "../../utils/supabase";
import { router } from "expo-router";
import { Input } from "../../components/Input";
import { InputSelect } from "../../components/InputSelect";
import useStyles from "../../styles/novoPedidoPage";
import { useTheme } from "../../hooks/useTheme";
import { Picker } from "@react-native-picker/picker";
import MoveLeft from "lucide-react-native/dist/esm/icons/move-left";
import { i18n } from "../../i18n"; // import do seu i18n configurado

export default function NovoPedidoScreen() {
  const { theme } = useTheme();
  const { colors, spacing } = theme;

  const styles = useStyles();
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

  const [loading, setLoading] = useState(false);
  const [isDatePickerVisible, setDatePickerVisibility] = useState(false);

  const showDatePicker = () => setDatePickerVisibility(true);
  const hideDatePicker = () => setDatePickerVisibility(false);

  const handleConfirm = (date: Date) => {
    handleChange("data_entrega", date);
    hideDatePicker();
  };

  const handleChange = (key: string, value: string | Date) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = async () => {
    if (!form.whatsapp || !form.texto_logo || !form.valor) {
      Alert.alert(i18n.t("Erro"), i18n.t("AlertaCamposObrigatorios"));
      return;
    }

    setLoading(true);

    try {
      const { error } = await supabase.from("Pedidos").insert([
        {
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
        },
      ]);

      if (error) throw error;

      ToastAndroid.show(i18n.t("PedidoCriadoSucesso"), 2000);
      setForm({
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
      router.back();
    } catch (err: any) {
      Alert.alert(i18n.t("Erro"), err.message);
    } finally {
      setLoading(false);
    }
  };

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
          <Text style={styles.title}>{i18n.t("NovoPedidoTitulo")}</Text>
        </View>

        <Text style={styles.subtitle}>{i18n.t("NovoPedidoSubtitulo")}</Text>

        <Input
          label={i18n.t("ClienteNovoPedido")}
          placeholder={i18n.t("ClientePlaceholder")}
          value={form.cliente}
          onChangeText={(v) => handleChange("cliente", v)}
        />
        <Input
          label={i18n.t("WhatsAppNovoPedido")}
          placeholder={i18n.t("WhatsAppPlaceholder")}
          keyboardType="numeric"
          value={form.whatsapp}
          onChangeText={(v) => handleChange("whatsapp", v)}
        />
        <Input
          label={i18n.t("TipoModeloNovoPedido")}
          placeholder={i18n.t("TipoModeloPlaceholder")}
          value={form.tipo_modelo}
          onChangeText={(v) => handleChange("tipo_modelo", v)}
        />
        <Input
          label={i18n.t("TextoLogoNovoPedido")}
          placeholder={i18n.t("TextoLogoPlaceholder")}
          value={form.texto_logo}
          onChangeText={(v) => handleChange("texto_logo", v)}
          multiline
        />
        <Input
          label={i18n.t("ObservacaoNovoPedido")}
          placeholder={i18n.t("ObservacaoPlaceholder")}
          value={form.observacao}
          onChangeText={(v) => handleChange("observacao", v)}
          multiline
        />
        <InputSelect
          label={i18n.t("TipoPagamentoNovoPedido")}
          value={form.tipo_pagamento}
          onChange={(v) => handleChange("tipo_pagamento", v)}
        />
        <Input
          label={i18n.t("ValorNovoPedido")}
          placeholder={i18n.t("ValorPlaceholder")}
          keyboardType="decimal-pad"
          value={form.valor}
          onChangeText={(v) => handleChange("valor", v)}
        />

        <View style={styles.dateSection}>
          <Text style={styles.label}>{i18n.t("DataEntregaNovoPedido")}</Text>
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
            {loading ? i18n.t("Enviando") : i18n.t("CadastrarPedido")}
          </Text>
        </TouchableOpacity>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
