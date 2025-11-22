import React, { useState, useEffect, useMemo, useCallback } from "react";
import { router, useFocusEffect } from "expo-router";
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  ActivityIndicator,
} from "react-native";
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
  // 🔹 BUSCA COM PAGINAÇÃO
  // ---------------------------
  const fetchPedidos = async (reset = false) => {
    try {
      if (reset) {
        setLoading(true);
        setPage(0);
        setHasMore(true);
      } else {
        if (!hasMore) return; // evita requisições desnecessárias
        setLoadingMore(true);
      }

      const start = reset ? 0 : page * PAGE_SIZE;
      const end = start + PAGE_SIZE - 1;

      // Monta a query com filtro direto no Supabase
      let query = supabase
        .from("Pedidos")
        .select("*")
        .order("id", { ascending: false })
        .range(start, end);

      if (statusFilter === "pendente") query = query.eq("entregue", false);
      else if (statusFilter === "concluido") query = query.eq("entregue", true);

      const { data, error } = await query;
      if (error) throw error;

      const fetched = data || [];

      if (reset) {
        setPedidos(fetched);
      } else {
        // evita duplicações
        setPedidos((prev) => {
          const ids = new Set(prev.map((p) => p.id));
          const novos = fetched.filter((p) => !ids.has(p.id));
          return [...prev, ...novos];
        });
      }

      if (fetched.length < PAGE_SIZE) setHasMore(false); // não há mais itens
      if (!reset) setPage((prev) => prev + 1);
    } catch (err: any) {
      console.error("Erro ao buscar pedidos:", err.message);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  };

  // Atualiza ao focar na tela (reset da paginação)
  useFocusEffect(
    useCallback(() => {
      fetchPedidos(true);
    }, [statusFilter]) // adiciona statusFilter para refazer a busca ao mudar filtro
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
                // aplica filtro no insert
                if (
                  (statusFilter === "pendente" && novo.entregue) ||
                  (statusFilter === "concluido" && !novo.entregue)
                )
                  return prev;
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
  }, [statusFilter]);

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
            onPress={() => {
              setStatusFilter(s as any);
            }}
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

      {/* Lista */}
      {loading ? (
        <PedidoCardSkeleton />
      ) : pedidos.length === 0 ? (
        <Text style={[styles.emptyText, { color: colors.textPrimary }]}>
          Nenhum pedido encontrado.
        </Text>
      ) : (
        <FlatList
          data={pedidos}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => (
            <PedidoCard
              pedido={item}
              onUpdate={(pedidoAtualizado) => {
                setPedidos((prev) =>
                  prev.map((p) =>
                    p.id === pedidoAtualizado.id ? pedidoAtualizado : p
                  )
                );
              }}
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
