// hooks/useTheme.js
import { useColorScheme } from "react-native";
import theme from "../theme";

export default function useTheme() {
  const colorScheme = useColorScheme(); // retorna "light" ou "dark"

  // fallback seguro caso algo venha undefined
  const selectedTheme = theme[colorScheme] || theme.light;

  return selectedTheme; // deve conter { colors, spacing }
}
