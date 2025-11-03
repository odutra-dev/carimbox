import { StyleSheet } from "react-native";
import useTheme from "../hooks/useTheme"; // ajuste o caminho conforme seu projeto

export const useStyles = () => {
  const { colors, spacing } = useTheme();

  return StyleSheet.create({
    container: {
      backgroundColor: colors.background,
      paddingVertical: spacing.xl,
      paddingHorizontal: spacing.md,
    },

    center: {
      justifyContent: "center",
      alignItems: "center",
      backgroundColor: colors.background,
    },

    header: {
      flexDirection: "row",
      alignItems: "center",
      justifyContent: "space-between",
      marginBottom: spacing.lg,
      backgroundColor: colors.background,
    },

    title: {
      fontSize: 22,
      fontWeight: "700",
      color: colors.textPrimary,
    },

    card: {
      backgroundColor: colors.surface,
      borderRadius: spacing.sm,
      padding: spacing.md,
      marginBottom: spacing.lg,
      borderWidth: 1,
      borderColor: colors.border,
      shadowColor: colors.black,
      shadowOffset: { width: 0, height: 2 },
      shadowOpacity: 0.08,
      shadowRadius: 3,
      elevation: 2,
    },

    voltarBtn: {
      flexDirection: "row",
      backgroundColor: colors.primary,
      paddingHorizontal: spacing.md,
      paddingVertical: spacing.sm + 2,
      borderRadius: spacing.sm,
      marginTop: spacing.md,
      alignItems: "center",
      gap: spacing.sm,
    },

    voltarText: {
      color: colors.white,
      fontSize: 15,
      fontWeight: "500",
    },

    copyButton: {
      flexDirection: "row",
      alignItems: "center",
      justifyContent: "center",
      backgroundColor: colors.primary,
      paddingVertical: spacing.md - 4,
      borderRadius: spacing.sm,
      marginTop: spacing.lg,
    },

    copyButtonText: {
      color: colors.white,
      fontSize: 16,
      fontWeight: "600",
    },
  });
};

export default useStyles;
