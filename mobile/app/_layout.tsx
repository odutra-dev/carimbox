import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";
import { ThemeProvider, useTheme } from "../hooks/useTheme";

function AppContent() {
  const { theme } = useTheme();
  const { colors } = theme;

  return (
    <>
      <StatusBar
        backgroundColor={colors.background}
        style={colors.background === "#F8FAFC" ? "dark" : "light"}
      />
      <Stack screenOptions={{ headerShown: false }}>
        <Stack.Screen name="(tabs)" />
        <Stack.Screen name="novoPedido/index" />
        <Stack.Screen name="verPedido/[id]" />
        <Stack.Screen name="atualizarPedido/[id]" />
      </Stack>
    </>
  );
}

export default function RootLayout() {
  return (
    <ThemeProvider>
      <AppContent />
    </ThemeProvider>
  );
}
