import React, { useState, useEffect, useCallback } from "react";
import { router, useFocusEffect } from "expo-router";
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  ActivityIndicator,
} from "react-native";
import DateTimePicker from "react-native-modal-datetime-picker";
import { supabase } from "../../utils/supabase";
import { PedidoCard, PedidoCardSkeleton } from "../../components/PedidoCard";
import { Pedido } from "../../@types/Pedido";
import { useTheme } from "../../hooks/useTheme";
import Plus from "lucide-react-native/dist/esm/icons/plus";
import useStyles from "../../styles/homepage";
import { i18n } from "../../i18n";

export default function PedidosScreen() {
  const styles = useStyles();
  const { theme } = useTheme();
  const colors = theme.colors;

  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const PAGE_SIZE = 20;

  const [statusFilter, setStatusFilter] = useState<
    "pendente" | "concluido" | "todos"
  >("pendente");

  // ---------------------------
  // 🔹 FILTRO DE DATA
  // ---------------------------
  type DateFilterType = "dia" | "mes" | "ano" | null;
  const [dateFilterType, setDateFilterType] = useState<DateFilterType>(null);
  const [dateFilterValue, setDateFilterValue] = useState<Date>(new Date());
  const [showPicker, setShowPicker] = useState(false);

  const formatDateBR = (d: Date) =>
    `${String(d.getDate()).padStart(2, "0")}/${String(
      d.getMonth() + 1
    ).padStart(2, "0")}/${d.getFullYear()}`;

  // -------------------------------------
  // 🔥 Transformar data local -> intervalo local sem UTC
  // -------------------------------------
  const startOfDate = (date: Date) => {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    return `${y}-${m}-${d} 00:00:00`;
  };

  const endOfDate = (date: Date) => {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, "0");
    const d = String(date.getDate()).padStart(2, "0");
    return `${y}-${m}-${d} 23:59:59`;
  };

  // ---------------------------
  // 🔹 GERA RANGE COMPLETO
  // ---------------------------
  const buildRange = () => {
    if (!dateFilterType) return null;

    const start = new Date(dateFilterValue);
    const end = new Date(dateFilterValue);

    if (dateFilterType === "dia") {
      // Ex: 5 DEZ 2025 → filtra só o mesmo dia
      return { start: startOfDate(start), end: endOfDate(start) };
    }

    if (dateFilterType === "mes") {
      // Primeiro dia do mês
      start.setDate(1);
      // Primeiro dia do próximo mês
      end.setMonth(end.getMonth() + 1);
      end.setDate(1);
      return { start: startOfDate(start), end: startOfDate(end) };
    }

    if (dateFilterType === "ano") {
      start.setMonth(0, 1);
      end.setFullYear(end.getFullYear() + 1);
      end.setMonth(0, 1);
      return { start: startOfDate(start), end: startOfDate(end) };
    }

    return null;
  };

  // ---------------------------
  // 🔹 BUSCA SUPABASE
  // ---------------------------
  const fetchPedidos = async (reset = false) => {
    try {
      if (reset) {
        setLoading(true);
        setPage(0);
        setHasMore(true);
      } else {
        if (!hasMore) return;
        setLoadingMore(true);
      }

      const startIndex = reset ? 0 : page * PAGE_SIZE;
      const endIndex = startIndex + PAGE_SIZE - 1;

      let query = supabase
        .from("Pedidos")
        .select("*")
        .order("id", { ascending: false })
        .range(startIndex, endIndex);

      // Status
      if (statusFilter === "pendente") query = query.eq("entregue", false);
      else if (statusFilter === "concluido") query = query.eq("entregue", true);

      // Data (finalmente corrigido!)
      const range = buildRange();
      if (range)
        query = query
          .gte("data_pedido", range.start)
          .lte("data_pedido", range.end);

      const { data, error } = await query;
      if (error) throw error;

      if (reset) setPedidos(data || []);
      else {
        const ids = new Set(pedidos.map((p) => p.id));
        const novos = (data || []).filter((p) => !ids.has(p.id));
        setPedidos((prev) => [...prev, ...novos]);
      }

      if (!data || data.length < PAGE_SIZE) setHasMore(false);
      if (!reset) setPage((p) => p + 1);
    } catch (e: any) {
      console.log("ERRO FETCH:", e.message);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  };

  // ---------------------------
  // 🔹 RELOAD EM FOCO E FILTROS
  // ---------------------------
  useFocusEffect(
    useCallback(() => {
      fetchPedidos(true);
    }, [statusFilter, dateFilterType, dateFilterValue])
  );

  // ---------------------------
  // 🔹 REALTIME
  // ---------------------------
  useEffect(() => {
    const channel = supabase
      .channel("rt-pedidos")
      .on(
        "postgres_changes",
        { event: "*", schema: "public", table: "Pedidos" },
        () => fetchPedidos(true)
      )
      .subscribe();

    return () => supabase.removeChannel(channel);
  }, []);

  // ---------------------------
  // 🔹 QUANDO O USUÁRIO CONFIRMA A DATA
  // ---------------------------
  const handleDateConfirm = (date: Date) => {
    setDateFilterValue(date);
    setShowPicker(false);
  };

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={[styles.title, { color: colors.textPrimary }]}>
          📦 {i18n.t("pedidosTitulo")}
        </Text>
        <Text style={[styles.subtitle, { color: colors.textPrimary }]}>
          {i18n.t("mensagemHome")}
        </Text>
      </View>

      {/* STATUS */}
      <View style={styles.toggleContainer}>
        {["pendente", "concluido", "todos"].map((s) => (
          <TouchableOpacity
            key={s}
            onPress={() => setStatusFilter(s as any)}
            style={[
              styles.toggleButton,
              {
                backgroundColor:
                  statusFilter === s ? colors.primary : colors.surface,
              },
            ]}
          >
            <Text
              style={{
                color: statusFilter === s ? colors.white : colors.textPrimary,
                fontWeight: "500",
              }}
            >
              {s === "todos"
                ? i18n.t("todos")
                : s === "pendente"
                ? i18n.t("pendentes")
                : i18n.t("concluidos")}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* TIPO DE DATA */}
      <View style={[styles.toggleContainer, { marginVertical: 10 }]}>
        {["dia", "mes", "ano"].map((tipo) => (
          <TouchableOpacity
            key={tipo}
            onPress={() => {
              setDateFilterType(tipo as DateFilterType);
              setShowPicker(true); // 🔥 obrigar escolher data
            }}
            style={[
              styles.toggleButton,
              {
                backgroundColor:
                  dateFilterType === tipo ? colors.primary : colors.surface,
              },
            ]}
          >
            <Text
              style={{
                color:
                  dateFilterType === tipo ? colors.white : colors.textPrimary,
                fontWeight: "500",
              }}
            >
              {i18n.t(tipo === "dia" ? "Dia" : tipo === "mes" ? "Mes" : "Ano")}
            </Text>
          </TouchableOpacity>
        ))}

        {/* Remover filtro */}
        <TouchableOpacity
          onPress={() => setDateFilterType(null)}
          style={[
            styles.toggleButton,
            { backgroundColor: colors.surface, marginLeft: 5 },
          ]}
        >
          <Text style={{ color: colors.textPrimary }}>{i18n.t("todos")}</Text>
        </TouchableOpacity>
      </View>

      {/* Date Picker */}
      <DateTimePicker
        isVisible={showPicker}
        mode="date"
        onConfirm={handleDateConfirm}
        onCancel={() => setShowPicker(false)}
        date={dateFilterValue}
        locale="pt-BR"
      />

      {/* LISTA */}
      {loading ? (
        <PedidoCardSkeleton />
      ) : pedidos.length === 0 ? (
        <Text style={{ textAlign: "center", color: colors.textPrimary }}>
          {i18n.t("NenhumPedidoEncontrado")}
        </Text>
      ) : (
        <FlatList
          data={pedidos}
          keyExtractor={(i) => i.id.toString()}
          renderItem={({ item }) => (
            <PedidoCard
              pedido={item}
              onUpdate={(p) =>
                setPedidos((prev) => prev.map((x) => (x.id === p.id ? p : x)))
              }
            />
          )}
          refreshing={loading}
          onRefresh={() => fetchPedidos(true)}
          onEndReached={() => !loadingMore && hasMore && fetchPedidos()}
          onEndReachedThreshold={0.3}
          ListFooterComponent={
            loadingMore ? (
              <ActivityIndicator size="large" color={colors.primary} />
            ) : null
          }
        />
      )}

      {/* BOTÃO */}
      <TouchableOpacity
        style={[
          styles.fab,
          { backgroundColor: colors.primary, shadowColor: colors.black },
        ]}
        onPress={() => router.navigate("novoPedido")}
      >
        <Plus size={32} color={colors.white} />
      </TouchableOpacity>
    </View>
  );
}
