import React from "react";
import { View, Text, Switch, StyleSheet } from "react-native";
import { useTheme } from "../../hooks/useTheme";
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
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
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
