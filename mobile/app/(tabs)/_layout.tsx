import { Tabs } from "expo-router";
import { useTheme } from "../../hooks/useTheme";
import Package from "lucide-react-native/dist/esm/icons/package";
import Settings from "lucide-react-native/dist/esm/icons/settings";

import { i18n } from "../../i18n";

export default function TabLayout() {
  const { theme } = useTheme();
  const { colors } = theme;
  const options = {
    headerShown: false,
    tabBarActiveTintColor: colors.primary,
    tabBarInactiveTintColor: colors.textSecondary,
    tabBarStyle: {
      backgroundColor: colors.background,
      borderTopColor: colors.border,
      borderTopWidth: 1,
    },
  };

  return (
    <Tabs screenOptions={options}>
      <Tabs.Screen
        name="index"
        options={{
          title: i18n.t("pedidosPage"),
          tabBarIcon: ({ color, size }) => (
            <Package size={size} color={color} />
          ),
        }}
      />
      <Tabs.Screen
        name="settings/index"
        options={{
          title: i18n.t("configuracoesPage"),
          tabBarIcon: ({ color, size }) => (
            <Settings size={size} color={color} />
          ),
        }}
      />
    </Tabs>
  );
}
