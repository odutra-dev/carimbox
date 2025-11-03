import { Text, ToastAndroid, TouchableOpacity, View } from "react-native";
import useTheme from "../../hooks/useTheme";
import { useStyles } from "./styles";
import * as Clipboard from "expo-clipboard";
import Copy from "lucide-react-native/dist/esm/icons/copy";

export const InfoRow = ({
  Icon,
  label,
  value,
}: {
  Icon: typeof Copy;
  label: string;
  value: string;
}) => {
  const { colors } = useTheme();
  const styles = useStyles();

  const handleCopyField = async () => {
    await Clipboard.setStringAsync(value);
    ToastAndroid.show("Texto copiado para a área de transferência.", 2000);
  };

  return (
    <View style={styles.infoRow}>
      <Icon size={18} color={colors.textSecondary} style={styles.icon} />
      <View style={{ flex: 1 }}>
        <Text style={styles.label}>{label}</Text>
        <Text style={styles.value}>{value}</Text>
      </View>
      <TouchableOpacity onPress={handleCopyField}>
        <Copy size={18} color={colors.textSecondary} />
      </TouchableOpacity>
    </View>
  );
};
