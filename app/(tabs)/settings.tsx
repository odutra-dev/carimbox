import React from "react";
import { View, Text, Switch, StyleSheet } from "react-native";
import { useTheme } from "../../hooks/useTheme";
import Constants from "expo-constants";

export default function SettingsScreen() {
  const { mode, toggleMode, theme } = useTheme();
  const { colors } = theme;

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      <Text style={[styles.title, { color: colors.textPrimary }]}>
        Configurações
      </Text>

      <View style={styles.row}>
        <Text style={[styles.label, { color: colors.textSecondary }]}>
          Modo Escuro
        </Text>
        <Switch value={mode === "dark"} onValueChange={toggleMode} />
      </View>

      <View
        style={{
          position: "absolute",
          bottom: 20,
          alignItems: "center",
          width: "100%",
        }}
      >
        <Text style={{ color: colors.textSecondary, fontSize: 12 }}>
          Versão: 1.0.0
        </Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 24,
    paddingVertical: 40,
  },
  title: {
    fontSize: 24,
    fontWeight: "bold",
    marginBottom: 20,
  },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginVertical: 10,
  },
  label: {
    fontSize: 18,
  },
});
