import React, { useState, useEffect, useCallback } from "react";
import { router, useFocusEffect } from "expo-router";
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  ActivityIndicator,
  Platform,
} from "react-native";
import DateTimePicker from "@react-native-community/datetimepicker";
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
  const [showDatePicker, setShowDatePicker] = useState(false);

  const formatDateBR = (date: Date) =>
    `${date.getDate().toString().padStart(2, "0")}/${(date.getMonth() + 1)
      .toString()
      .padStart(2, "0")}/${date.getFullYear()}`;

  const handleDateChange = (event: any, selectedDate?: Date) => {
    // Apenas atualizar quando o usuário confirmar
    if (event.type === "set" && selectedDate) {
      setDateFilterValue(selectedDate);
      fetchPedidos(true); // atualiza a lista imediatamente
    }
    setShowDatePicker(false);
  };

  // ---------------------------
  // 🔹 BUSCA COM PAGINAÇÃO E FILTROS
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

      const start = reset ? 0 : page * PAGE_SIZE;
      const end = start + PAGE_SIZE - 1;

      let query = supabase
        .from("Pedidos")
        .select("*")
        .order("id", { ascending: false })
        .range(start, end);

      // Filtro de status
      if (statusFilter === "pendente") query = query.eq("entregue", false);
      else if (statusFilter === "concluido") query = query.eq("entregue", true);

      // Filtro de data
      if (dateFilterType && dateFilterValue) {
        const startDate = new Date(dateFilterValue);
        let endDate = new Date(dateFilterValue);

        if (dateFilterType === "dia") endDate.setDate(startDate.getDate() + 1);
        else if (dateFilterType === "mes")
          endDate.setMonth(startDate.getMonth() + 1);
        else if (dateFilterType === "ano")
          endDate.setFullYear(startDate.getFullYear() + 1);

        // Considera apenas a parte da data para evitar problemas de UTC
        query = query
          .gte("data_pedido", startDate.toISOString().slice(0, 10))
          .lt("data_pedido", endDate.toISOString().slice(0, 10));
      }

      const { data, error } = await query;
      if (error) throw error;

      const fetched = data || [];

      if (reset) {
        setPedidos(fetched);
      } else {
        setPedidos((prev) => {
          const ids = new Set(prev.map((p) => p.id));
          const novos = fetched.filter((p) => !ids.has(p.id));
          return [...prev, ...novos];
        });
      }

      if (fetched.length < PAGE_SIZE) setHasMore(false);
      if (!reset) setPage((prev) => prev + 1);
    } catch (err: any) {
      console.error("Erro ao buscar pedidos:", err.message);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  };

  // Atualiza ao focar na tela ou ao mudar filtros
  useFocusEffect(
    useCallback(() => {
      fetchPedidos(true);
    }, [statusFilter, dateFilterType, dateFilterValue])
  );

  // ---------------------------
  // 🔹 REALTIME SUPABASE
  // ---------------------------
  useEffect(() => {
    const channel = supabase
      .channel("realtime-pedidos")
      .on(
        "postgres_changes",
        { event: "*", schema: "public", table: "Pedidos" },
        (payload) => {
          const novo = payload.new as Partial<Pedido>;
          const antigo = payload.old as Partial<Pedido>;

          setPedidos((prev) => {
            switch (payload.eventType) {
              case "INSERT":
                if (!novo.id) return prev;
                if (prev.find((p) => p.id === novo.id)) return prev;

                // Filtros
                if (
                  (statusFilter === "pendente" && novo.entregue) ||
                  (statusFilter === "concluido" && !novo.entregue)
                )
                  return prev;

                if (dateFilterType && dateFilterValue && novo.data_pedido) {
                  const created = new Date(novo.data_pedido);
                  const startDate = new Date(dateFilterValue);
                  let endDate = new Date(dateFilterValue);

                  if (dateFilterType === "dia")
                    endDate.setDate(startDate.getDate() + 1);
                  else if (dateFilterType === "mes")
                    endDate.setMonth(startDate.getMonth() + 1);
                  else if (dateFilterType === "ano")
                    endDate.setFullYear(startDate.getFullYear() + 1);

                  if (!(created >= startDate && created < endDate)) return prev;
                }

                return [novo as Pedido, ...prev];

              case "UPDATE":
                if (!novo.id) return prev;
                return prev.map((p) =>
                  p.id === novo.id ? (novo as Pedido) : p
                );

              case "DELETE":
                if (!antigo.id) return prev;
                return prev.filter((p) => p.id !== antigo.id);

              default:
                return prev;
            }
          });
        }
      )
      .subscribe();

    return () => supabase.removeChannel(channel);
  }, [statusFilter, dateFilterType, dateFilterValue]);

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

      {/* Toggle de status */}
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

      {/* Filtro de data */}
      <View style={[styles.toggleContainer, { marginVertical: 10 }]}>
        {["todos", "dia", "mes", "ano"].map((tipo) => (
          <TouchableOpacity
            key={tipo}
            onPress={() =>
              setDateFilterType(
                tipo === "todos" ? null : (tipo as DateFilterType)
              )
            }
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
              {tipo === "dia"
                ? i18n.t("Dia")
                : tipo === "mes"
                ? i18n.t("Mes")
                : tipo === "ano"
                ? i18n.t("Ano")
                : i18n.t("todos")}
            </Text>
          </TouchableOpacity>
        ))}

        {dateFilterType && (
          <TouchableOpacity
            onPress={() => setShowDatePicker(true)}
            style={[
              styles.toggleButton,
              { backgroundColor: colors.surface, marginLeft: 5 },
            ]}
          >
            <Text style={{ color: colors.textPrimary }}>
              {formatDateBR(dateFilterValue)}
            </Text>
          </TouchableOpacity>
        )}
      </View>

      {showDatePicker && (
        <DateTimePicker
          value={dateFilterValue}
          mode="date"
          display="default"
          onChange={handleDateChange}
          locale="pt-BR"
        />
      )}

      {/* Lista */}
      {loading ? (
        <PedidoCardSkeleton />
      ) : pedidos.length === 0 ? (
        <Text style={[styles.emptyText, { color: colors.textPrimary }]}>
          {i18n.t("NenhumPedidoEncontrado")}
        </Text>
      ) : (
        <FlatList
          data={pedidos}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <PedidoCard
              pedido={item}
              onUpdate={(pedidoAtualizado) =>
                setPedidos((prev) =>
                  prev.map((p) =>
                    p.id === pedidoAtualizado.id ? pedidoAtualizado : p
                  )
                )
              }
            />
          )}
          refreshing={loading}
          onRefresh={() => fetchPedidos(true)}
          onEndReached={() => {
            if (!loadingMore && hasMore) fetchPedidos();
          }}
          onEndReachedThreshold={0.3}
          ListFooterComponent={
            loadingMore ? (
              <ActivityIndicator size="large" color={colors.primary} />
            ) : null
          }
        />
      )}

      {/* Botão Novo Pedido */}
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
