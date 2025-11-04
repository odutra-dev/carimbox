import React, { useState, useEffect, useMemo, useCallback } from "react";
import { router, useFocusEffect } from "expo-router";
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  ActivityIndicator,
  StyleSheet,
} from "react-native";
import { supabase } from "../../utils/supabase";
import { PedidoCard } from "../../components/PedidoCard";
import { Pedido } from "../../@types/Pedido";
import { useTheme } from "../../hooks/useTheme";
import Plus from "lucide-react-native/dist/esm/icons/plus";

export default function PedidosScreen() {
  const { theme } = useTheme();
  const colors = theme.colors;

  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState<
    "pendente" | "concluido" | "todos"
  >("pendente");

  // Busca os pedidos
  const fetchPedidos = async () => {
    try {
      setLoading(true);
      const { data, error } = await supabase
        .from("Pedidos")
        .select("*")
        .order("id", { ascending: false });

      if (error) throw error;
      setPedidos(data || []);
    } catch (err: any) {
      console.error("Erro ao buscar pedidos:", err.message);
    } finally {
      setLoading(false);
    }
  };

  // Atualiza ao focar na tela
  useFocusEffect(
    useCallback(() => {
      fetchPedidos();
    }, [])
  );

  // Realtime Supabase v2
  useEffect(() => {
    fetchPedidos();

    const pedidosChannel = supabase
      .channel("realtime-pedidos")
      .on(
        "postgres_changes",
        { event: "*", schema: "public", table: "Pedidos" },
        (payload) => {
          console.log("Realtime payload:", payload);
          const newPedido: Pedido = payload.new;
          const oldPedido: Pedido = payload.old;

          setPedidos((prev) => {
            switch (payload.eventType) {
              case "INSERT":
                return [newPedido, ...prev];
              case "UPDATE":
                return prev.map((p) => (p.id === newPedido.id ? newPedido : p));
              case "DELETE":
                return prev.filter((p) => p.id !== oldPedido.id);
              default:
                return prev;
            }
          });
        }
      )
      .subscribe();

    return () => {
      supabase.removeChannel(pedidosChannel);
    };
  }, []);

  // Filtra pedidos de acordo com o status
  const filteredPedidos = useMemo(() => {
    return pedidos.filter((pedido) => {
      if (statusFilter === "todos") return true;
      if (statusFilter === "pendente") return !pedido.entregue;
      if (statusFilter === "concluido") return pedido.entregue;
      return true;
    });
  }, [pedidos, statusFilter]);

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={[styles.title, { color: colors.textPrimary }]}>
          📦 Pedidos
        </Text>
        <Text style={[styles.subtitle, { color: colors.textPrimary }]}>
          Acompanhe os pedidos recentes
        </Text>
      </View>

      {/* Toggle de status */}
      <View style={styles.toggleContainer}>
        {["pendente", "concluido", "todos"].map((s) => (
          <TouchableOpacity
            key={s}
            onPress={() => setStatusFilter(s as typeof statusFilter)}
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
                ? "Todos"
                : s === "pendente"
                ? "Pendentes"
                : "Concluídos"}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* Lista ou Loading */}
      {loading ? (
        <ActivityIndicator
          size="large"
          color={colors.primary}
          style={{ marginTop: 20 }}
        />
      ) : filteredPedidos.length === 0 ? (
        <Text style={[styles.emptyText, { color: colors.textPrimary }]}>
          Nenhum pedido encontrado.
        </Text>
      ) : (
        <FlatList
          data={filteredPedidos} // ✅ usar array filtrado
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
        />
      )}

      {/* Botão de novo pedido */}
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

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20 },
  header: { marginBottom: 10 },
  title: { fontSize: 28, fontWeight: "bold" },
  subtitle: { fontSize: 16, marginTop: 4 },
  toggleContainer: { flexDirection: "row", marginVertical: 10 },
  toggleButton: {
    flex: 1,
    paddingVertical: 8,
    marginHorizontal: 5,
    borderRadius: 8,
    alignItems: "center",
  },
  listContent: { paddingBottom: 100 },
  emptyText: { textAlign: "center", marginTop: 20, fontSize: 16 },
  fab: {
    position: "absolute",
    bottom: 20,
    right: 20,
    borderRadius: 50,
    width: 60,
    height: 60,
    justifyContent: "center",
    alignItems: "center",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.08,
    shadowRadius: 3,
    elevation: 2,
  },
});
