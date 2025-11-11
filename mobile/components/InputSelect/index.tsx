import React from "react";
import { View, Text } from "react-native";
import { Picker } from "@react-native-picker/picker";
import { useStyles } from "./styles";

export const InputSelect = ({ label, value, onChange }) => {
  const styles = useStyles();

  return (
    <View style={styles.inputContainer}>
      {label && <Text style={styles.label}>{label}</Text>}

      <View style={styles.pickerWrapper}>
        <Picker
          selectedValue={value}
          onValueChange={onChange}
          style={styles.picker}
        >
          <Picker.Item label="Selecione uma opção" value="" />
          <Picker.Item label="Pix" value="Pix" />
          <Picker.Item label="Cartão de Crédito" value="Cartão Crédito" />
          <Picker.Item label="Cartão de Débito" value="Cartão Débito" />
          <Picker.Item label="Dinheiro" value="Dinheiro" />
          <Picker.Item label="A pagar" value="A pagar" />
        </Picker>
      </View>
    </View>
  );
};
