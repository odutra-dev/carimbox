import React, { useCallback } from "react";
import {
  View,
  Text,
  Switch,
  StyleSheet,
  TouchableOpacity,
  ToastAndroid,
} from "react-native";
import { useTheme } from "../../../hooks/useTheme";
import * as updates from "expo-updates";
import RefreshCw from "lucide-react-native/dist/esm/icons/refresh-cw";
import { i18n, changeLanguage } from "../../../i18n";

export default function SettingsScreen() {
  const { mode, toggleMode, theme } = useTheme();
  const { colors } = theme;

  const checkForUpdate = useCallback(async () => {
    try {
      const update = await updates.checkForUpdateAsync();
      if (update.isAvailable) {
        await updates.fetchUpdateAsync();
        ToastAndroid.show(i18n.t("messageAtualizacao"), ToastAndroid.SHORT);
      } else {
        ToastAndroid.show(
          i18n.t("messageQueNaoAtualizacao"),
          ToastAndroid.SHORT
        );
      }
    } catch (error) {
      console.error("Erro ao verificar atualização:", error);
    }
  }, []);

  return (
    <View style={[styles.container, { backgroundColor: colors.background }]}>
      <Text style={[styles.title, { color: colors.textPrimary }]}>
        {i18n.t("confguracaoTitulo")}
      </Text>

      {/* Alternar modo escuro */}
      <View style={styles.row}>
        <Text style={[styles.label, { color: colors.textSecondary }]}>
          {i18n.t("modoEscuro")}
        </Text>
        <Switch
          value={mode === "dark"}
          onValueChange={toggleMode}
          accessibilityLabel="Alternar modo escuro"
          accessibilityHint="Ativa ou desativa o modo escuro"
        />
      </View>

      {/* Atualizações */}
      <View style={styles.updateContainer}>
        <View style={styles.row}>
          <Text style={[styles.label, { color: colors.textSecondary }]}>
            {i18n.t("verificarAtualizacao")}
          </Text>
          <TouchableOpacity onPress={checkForUpdate} activeOpacity={0.7}>
            <RefreshCw size={24} color={colors.textSecondary} />
          </TouchableOpacity>
        </View>
        <Text style={[styles.versionText, { color: colors.textSecondary }]}>
          {i18n.t("versao")}: 1.4.0
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
    gap: 16,
    borderBottomColor: "#222",
    borderBottomWidth: 1,
    paddingBottom: 16,
    marginBottom: 16,
  },
  label: {
    fontSize: 18,
  },
  updateContainer: {
    position: "absolute",
    bottom: 20,
    alignItems: "center",
    width: "100%",
  },
  versionText: {
    fontSize: 12,
    marginTop: 5,
  },
});
