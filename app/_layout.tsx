import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";
import useTheme from "../hooks/useTheme";

export default function RootLayout() {
  const { colors } = useTheme();

  return (
    <>
      <StatusBar
        backgroundColor={colors.background}
        style={colors.background === "#F8FAFC" ? "dark" : "light"}
      />
      <Stack screenOptions={{ headerShown: false }}>
        {/* Grupo de abas */}
        <Stack.Screen name="(tabs)" />

        <Stack.Screen name="novoPedido" />
        <Stack.Screen name="verPedido/[id]" />
        <Stack.Screen name="atualizarPedido/[id]" />
      </Stack>
    </>
  );
}
