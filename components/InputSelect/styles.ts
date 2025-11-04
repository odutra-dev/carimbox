import { StyleSheet } from "react-native";
import { useTheme } from "../../hooks/useTheme";

export const useStyles = () => {
  const { theme } = useTheme();
  const { colors, spacing } = theme;

  return StyleSheet.create({
    inputContainer: {
      marginBottom: spacing.md,
    },

    label: {
      fontSize: 14,
      color: colors.textPrimary,
      marginBottom: spacing.xs,
      fontWeight: "500",
    },

    pickerWrapper: {
      backgroundColor: colors.surface,
      borderRadius: spacing.sm,
      borderWidth: 1,
      borderColor: colors.border,
      overflow: "hidden",
    },

    picker: {
      height: 50,
      color: colors.textPrimary,
      paddingHorizontal: spacing.md,
    },
  });
};
