import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";
import { ThemeProvider, useTheme } from "../hooks/useTheme";

//Localization
import { getLocales } from "expo-localization";
import { i18n } from "../i18n";
function AppContent() {
  const { theme } = useTheme();
  const { colors } = theme;

  i18n.locale = getLocales()[0].languageCode ?? "en";

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
