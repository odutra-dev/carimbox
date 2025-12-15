import React, { useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  TextInput,
  FlatList,
  ActivityIndicator,
} from "react-native";
import DateTimePicker from "react-native-modal-datetime-picker";
import { supabase } from "../../../utils/supabase";
import { PedidoCard, PedidoCardSkeleton } from "../../../components/PedidoCard";
import { Pedido } from "../../../@types/Pedido";
import { useTheme } from "../../../hooks/useTheme";
import { i18n } from "../../../i18n";

type DateFilterType = "dia" | "mes" | "ano" | null;

export default function SearchScreen() {
  const { theme } = useTheme();
  const { colors } = theme;

  const [pedidoId, setPedidoId] = useState("");
  const [dateFilterType, setDateFilterType] = useState<DateFilterType>(null);
  const [dateFilterValue, setDateFilterValue] = useState(new Date());
  const [showPicker, setShowPicker] = useState(false);

  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);

  // 🔹 Date helpers (igual ao pedidos)
  const startOfDate = (d: Date) =>
    `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(
      d.getDate()
    ).padStart(2, "0")} 00:00:00`;

  const endOfDate = (d: Date) =>
    `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(
      d.getDate()
    ).padStart(2, "0")} 23:59:59`;

  const buildRange = () => {
    if (!dateFilterType) return null;

    const start = new Date(dateFilterValue);
    const end = new Date(dateFilterValue);

    if (dateFilterType === "dia")
      return { start: startOfDate(start), end: endOfDate(start) };

    if (dateFilterType === "mes") {
      start.setDate(1);
      end.setMonth(end.getMonth() + 1, 1);
      return { start: startOfDate(start), end: startOfDate(end) };
    }

    if (dateFilterType === "ano") {
      start.setMonth(0, 1);
      end.setFullYear(end.getFullYear() + 1, 0, 1);
      return { start: startOfDate(start), end: startOfDate(end) };
    }

    return null;
  };

  // 🔥 BUSCA REAL
  const aplicarFiltros = async () => {
    try {
      setLoading(true);
      setSearched(true);

      let query = supabase
        .from("Pedidos")
        .select("*")
        .order("id", { ascending: false });

      // 🔍 ID
      if (pedidoId) {
        query = query.eq("id", Number(pedidoId));
      }

      // 📅 Data
      const range = buildRange();
      if (range) {
        query = query
          .gte("data_pedido", range.start)
          .lte("data_pedido", range.end);
      }

      const { data, error } = await query;
      if (error) throw error;

      setPedidos(data || []);
    } catch (e: any) {
      console.log("ERRO SEARCH:", e.message);
    } finally {
      setLoading(false);
    }
  };

  const limparFiltros = () => {
    setPedidoId("");
    setDateFilterType(null);
    setDateFilterValue(new Date());
    setPedidos([]);
    setSearched(false);
  };

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      {/* FILTROS */}
      <Text style={[styles.title, { color: colors.textPrimary }]}>
        {i18n.t("searchTitulo")}
      </Text>

      <Text style={{ color: colors.textPrimary }}>{i18n.t("BuscarPorId")}</Text>
      <TextInput
        style={[
          styles.input,
          { backgroundColor: colors.surface, color: colors.textPrimary },
        ]}
        keyboardType="numeric"
        placeholder="Ex: 123"
        placeholderTextColor={colors.textSecondary}
        value={pedidoId}
        onChangeText={setPedidoId}
      />

      <View style={styles.row}>
        {["dia", "mes", "ano"].map((tipo) => (
          <TouchableOpacity
            key={tipo}
            style={[
              styles.filterButton,
              {
                backgroundColor:
                  dateFilterType === tipo ? colors.primary : colors.surface,
              },
            ]}
            onPress={() => {
              setDateFilterType(tipo as DateFilterType);
              setShowPicker(true);
            }}
          >
            <Text
              style={{
                color:
                  dateFilterType === tipo ? colors.white : colors.textPrimary,
              }}
            >
              {tipo.toUpperCase()}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      <DateTimePicker
        isVisible={showPicker}
        mode="date"
        date={dateFilterValue}
        onConfirm={(d) => {
          setDateFilterValue(d);
          setShowPicker(false);
        }}
        onCancel={() => setShowPicker(false)}
      />

      <TouchableOpacity
        style={[styles.applyButton, { backgroundColor: colors.primary }]}
        onPress={aplicarFiltros}
      >
        <Text style={{ color: colors.white, fontWeight: "bold" }}>
          {i18n.t("AplicarFiltros")}
        </Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={[styles.clearButton, { borderColor: colors.primary }]}
        onPress={limparFiltros}
      >
        <Text style={{ color: colors.primary, fontWeight: "bold" }}>
          {i18n.t("LimparFiltros")}
        </Text>
      </TouchableOpacity>

      {/* RESULTADO */}
      {loading ? (
        <PedidoCardSkeleton />
      ) : searched && pedidos.length === 0 ? (
        <Text
          style={{
            textAlign: "center",
            marginTop: 20,
            color: colors.textPrimary,
          }}
        >
          {i18n.t("NenhumPedidoEncontrado")}
        </Text>
      ) : (
        <FlatList
          data={pedidos}
          keyExtractor={(i) => i.id.toString()}
          renderItem={({ item }) => <PedidoCard pedido={item} />}
          contentContainerStyle={{ paddingBottom: 40 }}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 24,
  },
  title: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 20,
  },
  input: {
    borderRadius: 8,
    padding: 12,
    marginVertical: 10,
  },
  row: {
    flexDirection: "row",
    gap: 10,
    marginVertical: 20,
  },
  filterButton: {
    flex: 1,
    padding: 12,
    borderRadius: 8,
    alignItems: "center",
  },
  applyButton: {
    marginBottom: 20,
    padding: 16,
    borderRadius: 10,
    alignItems: "center",
  },
  clearButton: {
    marginBottom: 20,
    padding: 14,
    borderRadius: 10,
    alignItems: "center",
    borderWidth: 1,
  },
});
