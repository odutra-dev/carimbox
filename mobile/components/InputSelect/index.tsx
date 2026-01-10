import React from "react";
import { View, Text } from "react-native";
import { Picker } from "@react-native-picker/picker";
import { useStyles } from "./styles";
import { i18n } from "../../i18n";

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
          <Picker.Item label={i18n.t("SelecioneUmaOpcao")} value="" />
          <Picker.Item label={i18n.t("Pix")} value="Pix" />
          <Picker.Item
            label={i18n.t("CartaoDeCredito")}
            value="Cartão Crédito"
          />
          <Picker.Item label={i18n.t("CartaoDeDebito")} value="Cartão Débito" />
          <Picker.Item label={i18n.t("Dinheiro")} value="Dinheiro" />
          <Picker.Item label={i18n.t("LinkPay")} value="Link de pagamento" />
          <Picker.Item label={i18n.t("APagar")} value="A pagar" />
        </Picker>
      </View>
    </View>
  );
};
