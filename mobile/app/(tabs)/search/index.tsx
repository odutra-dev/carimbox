import React, { useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  TextInput,
  FlatList,
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
  const { colors, spacing } = theme;

  const [pedidoId, setPedidoId] = useState("");
  const [dateFilterType, setDateFilterType] = useState<DateFilterType>(null);
  const [dateFilterValue, setDateFilterValue] = useState(new Date());
  const [showPicker, setShowPicker] = useState(false);

  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);

  /* =======================
      Date helpers
  ======================== */
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

  /* =======================
      Actions
  ======================== */
  const aplicarFiltros = async () => {
    try {
      setLoading(true);
      setSearched(true);

      let query = supabase
        .from("Pedidos")
        .select("*")
        .order("id", { ascending: false });

      if (pedidoId) query = query.eq("id", Number(pedidoId));

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
      console.log("SEARCH ERROR:", e.message);
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

  const filtrosAtivos = pedidoId !== "" || dateFilterType !== null;

  /* =======================
      UI
  ======================== */
  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      {/* HEADER */}
      <View style={styles.header}>
        <Text style={[styles.title, { color: colors.textPrimary }]}>
          {i18n.t("searchTitulo")}
        </Text>
        <Text style={[styles.subtitle, { color: colors.textSecondary }]}>
          {i18n.t("BuscarPorId")} / {i18n.t("Dia")} • {i18n.t("Mes")} •{" "}
          {i18n.t("Ano")}
        </Text>
      </View>

      {/* FILTER CARD */}
      <View
        style={[
          styles.card,
          {
            backgroundColor: colors.surface,
            borderColor: colors.border,
          },
        ]}
      >
        {/* ID */}
        <Text style={[styles.label, { color: colors.textSecondary }]}>
          {i18n.t("BuscarPorId")}
        </Text>
        <TextInput
          style={[
            styles.input,
            {
              backgroundColor: colors.background,
              color: colors.textPrimary,
              borderColor: colors.border,
            },
          ]}
          keyboardType="numeric"
          placeholder="Ex: 123"
          placeholderTextColor={colors.textMuted}
          value={pedidoId}
          onChangeText={setPedidoId}
        />

        {/* DATE TYPE */}
        <Text style={[styles.label, { color: colors.textSecondary }]}>
          {i18n.t("DataPedido")}
        </Text>
        <View style={styles.row}>
          {["dia", "mes", "ano"].map((tipo) => (
            <TouchableOpacity
              key={tipo}
              style={[
                styles.filterButton,
                {
                  backgroundColor:
                    dateFilterType === tipo
                      ? colors.primary
                      : colors.background,
                  borderColor: colors.border,
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
                  fontWeight: "500",
                }}
              >
                {i18n.t(
                  tipo === "dia" ? "Dia" : tipo === "mes" ? "Mes" : "Ano"
                )}
              </Text>
            </TouchableOpacity>
          ))}
        </View>

        {/* ACTIONS */}
        <View style={styles.actions}>
          <TouchableOpacity
            style={[styles.applyButton, { backgroundColor: colors.primary }]}
            onPress={aplicarFiltros}
          >
            <Text style={{ color: colors.white, fontWeight: "bold" }}>
              {i18n.t("AplicarFiltros")}
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            disabled={!filtrosAtivos}
            style={[
              styles.clearButton,
              {
                borderColor: colors.primary,
                opacity: filtrosAtivos ? 1 : 0.4,
              },
            ]}
            onPress={limparFiltros}
          >
            <Text
              style={{
                color: colors.primary,
                fontWeight: "bold",
              }}
            >
              {i18n.t("LimparFiltros")}
            </Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* RESULTS */}
      <View style={styles.results}>
        {loading ? (
          <PedidoCardSkeleton />
        ) : searched && pedidos.length === 0 ? (
          <Text
            style={{
              textAlign: "center",
              color: colors.textMuted,
              marginTop: spacing.lg,
            }}
          >
            {i18n.t("NenhumPedidoEncontrado")}
          </Text>
        ) : (
          <FlatList
            data={pedidos}
            keyExtractor={(i) => i.id.toString()}
            renderItem={({ item }) => <PedidoCard pedido={item} />}
            contentContainerStyle={{ paddingBottom: spacing.xl }}
          />
        )}
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
    </View>
  );
}

/* =======================
    Styles
======================== */
const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 24,
    paddingVertical: 40,
  },

  header: {
    paddingBottom: 12,
  },

  title: {
    fontSize: 24,
    fontWeight: "700",
  },

  subtitle: {
    marginTop: 4,
    fontSize: 14,
  },

  card: {
    padding: 16,
    borderRadius: 16,
    borderWidth: 1,
  },

  label: {
    fontSize: 13,
    marginBottom: 6,
    fontWeight: "500",
  },

  input: {
    borderRadius: 10,
    padding: 12,
    borderWidth: 1,
    marginBottom: 16,
  },

  row: {
    flexDirection: "row",
    gap: 8,
    marginBottom: 16,
  },

  filterButton: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 10,
    alignItems: "center",
    borderWidth: 1,
  },

  actions: {
    flexDirection: "row",
    gap: 12,
  },

  applyButton: {
    flex: 1,
    paddingVertical: 14,
    borderRadius: 12,
    alignItems: "center",
  },

  clearButton: {
    flex: 1,
    paddingVertical: 14,
    borderRadius: 12,
    alignItems: "center",
    borderWidth: 1,
  },

  results: {
    flex: 1,
    marginTop: 16,
    paddingHorizontal: 16,
  },
});
