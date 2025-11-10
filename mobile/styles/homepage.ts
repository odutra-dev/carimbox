import { StyleSheet } from "react-native";
import { useTheme } from "../hooks/useTheme";

export const useStyles = () => {
  const { theme } = useTheme();
  const { colors, spacing } = theme;

  return StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: colors.background,
      paddingVertical: spacing.xl,
      paddingHorizontal: spacing.md,
    },
    header: {
      paddingHorizontal: spacing.xl,
      marginTop: spacing.xl,
      paddingBottom: spacing.xs,
    },
    title: {
      fontSize: 26,
      fontWeight: "700",
      color: colors.textPrimary,
    },
    subtitle: {
      fontSize: 14,
      color: colors.textSecondary,
      marginTop: spacing.xs / 2,
    },
    listContent: {
      paddingHorizontal: spacing.md,
      paddingBottom: spacing.lg,
    },
    emptyText: {
      textAlign: "center",
      marginTop: spacing.xl,
      color: colors.textMuted,
      fontSize: 16,
    },
    toggleContainer: { flexDirection: "row", marginVertical: 10 },
    toggleButton: {
      flex: 1,
      paddingVertical: 8,
      marginHorizontal: 5,
      borderRadius: 8,
      alignItems: "center",
    },

    fab: {
      position: "absolute",
      bottom: 20,
      right: 20,
      borderRadius: 50,
      width: 60,
      height: 60,
      justifyContent: "center",
      alignItems: "center",
      shadowOffset: { width: 0, height: 2 },
      shadowOpacity: 0.08,
      shadowRadius: 3,
      elevation: 2,
    },
  });
};

export default useStyles;
