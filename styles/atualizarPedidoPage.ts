import { StyleSheet } from "react-native";
import useTheme from "../hooks/useTheme";

export const useStyles = () => {
  const { colors, spacing } = useTheme();

  return StyleSheet.create({
    container: {
      backgroundColor: colors.background,
      paddingVertical: spacing.xl,
      paddingHorizontal: spacing.md,
    },

    title: {
      fontSize: 26,
      fontWeight: "700",
      color: colors.textPrimary,
      marginBottom: spacing.sm,
    },

    subtitle: {
      fontSize: 14,
      color: colors.textSecondary,
      marginBottom: spacing.lg,
    },

    label: {
      fontSize: 14,
      color: colors.textPrimary,
      marginBottom: spacing.xs,
      fontWeight: "500",
    },

    dateSection: {
      marginBottom: spacing.md,
    },

    dateButton: {
      backgroundColor: colors.surface,
      borderRadius: spacing.sm,
      paddingVertical: spacing.md,
      paddingHorizontal: spacing.md,
      borderWidth: 1,
      borderColor: colors.border,
    },

    dateText: {
      fontSize: 15,
      color: colors.textPrimary,
    },

    button: {
      backgroundColor: colors.primary,
      borderRadius: spacing.sm,
      paddingVertical: spacing.md + 2,
      alignItems: "center",
      marginTop: spacing.md,
      shadowColor: colors.black,
      shadowOffset: { width: 0, height: 2 },
      shadowOpacity: 0.1,
      shadowRadius: 3,
      elevation: 2,
    },

    buttonText: {
      color: colors.white,
      fontWeight: "600",
      fontSize: 16,
    },

    loadingContainer: {
      flex: 1,
      justifyContent: "center",
      alignItems: "center",
      backgroundColor: colors.background,
    },
  });
};

export default useStyles;
