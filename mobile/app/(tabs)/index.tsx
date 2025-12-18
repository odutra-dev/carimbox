import React, { useState, useEffect, useCallback } from "react";
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  ActivityIndicator,
} from "react-native";
import { router, useFocusEffect, useLocalSearchParams } from "expo-router";
import { supabase } from "../../utils/supabase";
import { PedidoCard, PedidoCardSkeleton } from "../../components/PedidoCard";
import { Pedido } from "../../@types/Pedido";
import { useTheme } from "../../hooks/useTheme";
import Plus from "lucide-react-native/dist/esm/icons/plus";
import useStyles from "../../styles/homepage";
import { i18n } from "../../i18n";

type DateFilterType = "dia" | "mes" | "ano";

export default function PedidosScreen() {
  const styles = useStyles();
  const { theme } = useTheme();
  const colors = theme.colors;

  const params = useLocalSearchParams<{
    pedidoId?: string;
    dateFilterType?: DateFilterType;
    date?: string;
  }>();

  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);

  const PAGE_SIZE = 20;

  const [statusFilter, setStatusFilter] = useState<
    "pendente" | "concluido" | "todos"
  >("pendente");

  const [dateFilterType, setDateFilterType] = useState<DateFilterType | null>(
    null
  );
  const [dateFilterValue, setDateFilterValue] = useState<Date>(new Date());

  // 🔹 Sync params → state
  useEffect(() => {
    if (params.dateFilterType && params.date) {
      setDateFilterType(params.dateFilterType);
      setDateFilterValue(new Date(params.date));
    } else {
      setDateFilterType(null);
    }
  }, [params.dateFilterType, params.date]);

  // 🔹 Date helpers
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

    if (dateFilterType === "dia") {
      return { start: startOfDate(start), end: endOfDate(start) };
    }

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

  // 🔹 Fetch
  const fetchPedidos = async (reset = false) => {
    try {
      reset ? setLoading(true) : setLoadingMore(true);

      if (reset) {
        setPage(0);
        setHasMore(true);
      }

      const startIndex = reset ? 0 : page * PAGE_SIZE;
      const endIndex = startIndex + PAGE_SIZE - 1;

      let query = supabase
        .from("Pedidos")
        .select("*")
        .order("id", { ascending: false })
        .range(startIndex, endIndex);

      // 🔍 ID
      if (params.pedidoId) {
        query = query.eq("id", Number(params.pedidoId));
        setHasMore(false);
      }

      // 📦 Status
      if (statusFilter === "pendente") query = query.eq("entregue", false);
      if (statusFilter === "concluido") query = query.eq("entregue", true);

      // 📅 Data
      const range = buildRange();
      if (range) {
        query = query
          .gte("data_pedido", range.start)
          .lte("data_pedido", range.end);
      }

      const { data, error } = await query;
      if (error) throw error;

      setPedidos(reset ? data || [] : (prev) => [...prev, ...(data || [])]);

      if (!data || data.length < PAGE_SIZE) setHasMore(false);
      if (!reset && !params.pedidoId) setPage((p) => p + 1);
    } catch (e: any) {
      console.log("ERRO:", e.message);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  };

  // 🔄 Reload
  useFocusEffect(
    useCallback(() => {
      fetchPedidos(true);
    }, [statusFilter, params.pedidoId, params.dateFilterType, params.date])
  );

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      {/* HEADER */}
      <View style={styles.header}>
        <View>
          <Text style={[styles.title, { color: colors.textPrimary }]}>
            📦 {i18n.t("pedidosTitulo")}
          </Text>
          <Text style={[styles.subtitle, { color: colors.textPrimary }]}>
            {i18n.t("mensagemHome")}
          </Text>
        </View>
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
            <PedidoCard pedido={item} onUpdate={() => fetchPedidos(true)} />
          )}
          onEndReached={() => !loadingMore && hasMore && fetchPedidos(false)}
          onEndReachedThreshold={0.3}
          ListFooterComponent={
            loadingMore ? (
              <ActivityIndicator size="large" color={colors.primary} />
            ) : null
          }
          onRefresh={() => fetchPedidos(true)}
          refreshing={loading}
        />
      )}

      {/* FAB */}
      <TouchableOpacity
        style={[
          styles.fab,
          { backgroundColor: colors.primary, shadowColor: colors.black },
        ]}
        onPress={() => router.push("novoPedido")}
      >
        <Plus size={32} color={colors.white} />
      </TouchableOpacity>
    </View>
  );
}
