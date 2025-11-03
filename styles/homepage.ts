import { StyleSheet } from "react-native";
import useTheme from "../hooks/useTheme";

export const useStyles = () => {
  const { colors, spacing } = useTheme();

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
  });
};

export default useStyles;
