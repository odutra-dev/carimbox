import { StyleSheet } from "react-native";
import { useTheme } from "../../hooks/useTheme";

export const useStyles = () => {
  const { theme } = useTheme();
  const { colors, spacing } = theme;

  return StyleSheet.create({
    card: {
      width: "100%",
      padding: spacing.md,
      marginVertical: spacing.xs,
      borderRadius: spacing.sm,
      backgroundColor: colors.surface,
      shadowColor: colors.black,
      shadowOffset: { width: 0, height: 2 },
      shadowOpacity: 0.08,
      shadowRadius: 3,
      elevation: 2,
    },
    header: {
      flexDirection: "row",
      justifyContent: "space-between",
      alignItems: "center",
    },
    idText: {
      fontSize: 16,
      fontWeight: "bold",
      color: colors.textPrimary,
    },
    dataText: {
      fontSize: 12,
      color: colors.textSecondary,
    },
    clienteText: {
      fontSize: 14,
      color: colors.textPrimary,
      marginTop: spacing.xs,
    },
    iconContainer: {
      flexDirection: "row",
      alignItems: "center",
      gap: spacing.xl,
    },
    icon: {
      color: colors.primary,
    },
    iconCheck: {
      color: colors.success,
    },
    skelBase: {
      backgroundColor: colors.surface
        ? colors.surface
        : "rgba(180,180,180,0.35)",

      borderRadius: spacing.xs,
    },
    skelPlaceholder: {
      backgroundColor: colors.border,
      borderRadius: spacing.xs,
    },
    line: {
      height: 14,
      backgroundColor: colors.border,
      borderRadius: spacing.xs,
      marginTop: spacing.sm,
    },
  });
};
