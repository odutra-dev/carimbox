import React, { useState, useEffect } from "react";
import { router } from "expo-router";
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  ActivityIndicator,
} from "react-native";
import { StatusBar } from "expo-status-bar";
import { supabase } from "../../utils/supabase";
import { PedidoCard } from "../../components/PedidoCard";
import { Pedido } from "../../@types/Pedido";
import useStyles from "../../styles/homepage";
import useTheme from "../../hooks/useTheme"; // 👈 importar o hook de tema
import Plus from "lucide-react-native/dist/esm/icons/plus";

export default function PedidosScreen() {
  const styles = useStyles();
  const { colors } = useTheme(); // 👈 tema atual (light/dark)

  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const getPedidos = async () => {
      try {
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

    getPedidos();
  }, []);

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>📦 Pedidos</Text>
        <Text style={styles.subtitle}>Acompanhe os pedidos recentes</Text>
      </View>

      {loading ? (
        <ActivityIndicator
          size="large"
          color={colors.primary} // 👈 cor primária do tema
          style={{ marginTop: 20 }}
        />
      ) : pedidos.length === 0 ? (
        <Text style={styles.emptyText}>Nenhum pedido encontrado.</Text>
      ) : (
        <FlatList
          data={pedidos}
          keyExtractor={(item) => item.id.toString()}
          renderItem={({ item }) => <PedidoCard pedido={item} />}
          showsVerticalScrollIndicator={false}
          contentContainerStyle={styles.listContent}
        />
      )}

      <TouchableOpacity
        style={{
          position: "absolute",
          bottom: 100,
          right: 20,
          backgroundColor: colors.primary,
          borderRadius: 50,
          width: 60,
          height: 60,
          justifyContent: "center",
          alignItems: "center",
          shadowColor: colors.black,
          shadowOffset: { width: 0, height: 2 },
          shadowOpacity: 0.08,
          shadowRadius: 3,
          elevation: 2,
        }}
        onPress={() => router.navigate("novoPedido")}
      >
        <Plus size={32} color={colors.white} />
      </TouchableOpacity>
    </View>
  );
}
