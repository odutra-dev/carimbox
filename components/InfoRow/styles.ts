import { StyleSheet } from "react-native";
import useTheme from "../../hooks/useTheme";

export const useStyles = () => {
  const { colors, spacing } = useTheme(); // ✅ move o hook para dentro da função

  return StyleSheet.create({
    infoRow: {
      flexDirection: "row",
      alignItems: "center",
      marginBottom: spacing.md - 2,
    },

    icon: {
      marginRight: spacing.sm,
    },

    label: {
      fontSize: 13,
      color: colors.textSecondary,
      marginBottom: spacing.xs,
    },

    value: {
      fontSize: 15,
      color: colors.textPrimary,
      fontWeight: "500",
    },
  });
};
