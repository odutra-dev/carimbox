import { StyleSheet } from "react-native";
import { useTheme } from "../../hooks/useTheme";

export const useStyles = () => {
  const { theme } = useTheme(); // ✅ move o hook para dentro da função
  const { colors, spacing } = theme;
  return StyleSheet.create({
    inputContainer: {
      marginBottom: spacing.md,
    },
    label: {
      fontSize: 14,
      color: colors.textSecondary,
      marginBottom: spacing.xs,
      fontWeight: "500",
    },
    input: {
      backgroundColor: colors.surface,
      color: colors.textPrimary,
      padding: spacing.md,
      borderRadius: spacing.sm,
      borderWidth: 1,
      borderColor: colors.border,
      textAlignVertical: "center",
      fontSize: 15,
    },
    multilineInput: {
      height: 100,
      textAlignVertical: "top",
    },
  });
};
