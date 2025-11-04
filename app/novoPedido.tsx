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
import { supabase } from "../utils/supabase";
import { router } from "expo-router";
import { Input } from "../components/Input";
import { InputSelect } from "../components/InputSelect";
import useStyles from "../styles/novoPedidoPage";
import { useTheme } from "../hooks/useTheme";

import MoveLeft from "lucide-react-native/dist/esm/icons/move-left";

export default function NovoPedidoScreen() {
  const { theme } = useTheme();
  const { colors } = theme;

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
    if (!form.cliente || !form.whatsapp || !form.tipo_modelo || !form.valor) {
      Alert.alert("Atenção", "Preencha todos os campos obrigatórios!");
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
        },
      ]);

      if (error) throw error;

      ToastAndroid.show("Pedido criado com sucesso!", 2000);
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
      });
      router.back();
    } catch (err: any) {
      Alert.alert("Erro", err.message);
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
          <Text style={styles.title}>📝 Novo Pedido</Text>
        </View>
        <Text style={styles.subtitle}>
          Preencha os dados abaixo para registrar um novo pedido
        </Text>

        {/* Campos do formulário */}
        <Input
          label="Cliente *"
          placeholder="Nome do cliente"
          value={form.cliente}
          onChangeText={(v) => handleChange("cliente", v)}
        />
        <Input
          label="WhatsApp *"
          placeholder="Ex: 5511999999999"
          keyboardType="numeric"
          value={form.whatsapp}
          onChangeText={(v) => handleChange("whatsapp", v)}
        />
        <Input
          label="Modelo *"
          placeholder="Ex: Automático, Trodat 4911"
          value={form.tipo_modelo}
          onChangeText={(v) => handleChange("tipo_modelo", v)}
        />
        <Input
          label="Descrição *"
          placeholder="Digite o texto do carimbo"
          value={form.texto_logo}
          onChangeText={(v) => handleChange("texto_logo", v)}
          multiline
        />
        <Input
          label="Observações"
          placeholder="Algum detalhe adicional?"
          value={form.observacao}
          onChangeText={(v) => handleChange("observacao", v)}
          multiline
        />
        <InputSelect
          label="Forma de Pagamento *"
          value={form.tipo_pagamento}
          onChange={(v) => handleChange("tipo_pagamento", v)}
        />

        <Input
          label="Valor *"
          placeholder="Ex: 79.90"
          keyboardType="decimal-pad"
          value={form.valor}
          onChangeText={(v) => handleChange("valor", v)}
        />

        {/* Data da entrega */}
        <View style={styles.dateSection}>
          <Text style={styles.label}>Data de entrega *</Text>
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

        {/* Botão de envio */}
        <TouchableOpacity
          style={[styles.button, loading && { opacity: 0.6 }]}
          onPress={handleSubmit}
          disabled={loading}
        >
          <Text style={styles.buttonText}>
            {loading ? "Enviando..." : "Cadastrar Pedido"}
          </Text>
        </TouchableOpacity>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
