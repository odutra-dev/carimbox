import React from "react";
import { View, Text, TextInput } from "react-native";
import { useStyles } from "./styles";
import { useTheme } from "../../hooks/useTheme";

export const Input = ({ label, multiline = false, ...props }) => {
  const styles = useStyles();
  const { theme } = useTheme();
  const { colors } = theme;

  return (
    <View style={styles.inputContainer}>
      {label && <Text style={styles.label}>{label}</Text>}

      <TextInput
        style={[styles.input, multiline && styles.multilineInput]}
        placeholderTextColor={colors.textSecondary}
        multiline={multiline}
        {...props}
      />
    </View>
  );
};
