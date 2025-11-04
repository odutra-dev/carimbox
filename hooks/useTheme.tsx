import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
  ReactNode,
} from "react";
import { useColorScheme } from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import theme from "../theme";

const STORAGE_KEY = "@user_theme_mode";

export type ThemeMode = "light" | "dark";

export type ThemeContextType = {
  mode: ThemeMode;
  toggleMode: () => void;
  theme: typeof theme.light; // assume que theme.light tem { colors, spacing }
};

type ThemeProviderProps = {
  children: ReactNode;
};

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const ThemeProvider = ({ children }: ThemeProviderProps) => {
  const systemColorScheme = useColorScheme();
  const [mode, setMode] = useState<ThemeMode>("light");

  useEffect(() => {
    (async () => {
      const savedMode = await AsyncStorage.getItem(STORAGE_KEY);
      if (savedMode === "light" || savedMode === "dark") {
        setMode(savedMode);
      } else {
        setMode((systemColorScheme as ThemeMode) || "light");
      }
    })();
  }, [systemColorScheme]);

  const toggleMode = useCallback(async () => {
    const newMode: ThemeMode = mode === "light" ? "dark" : "light";
    setMode(newMode);
    await AsyncStorage.setItem(STORAGE_KEY, newMode);
  }, [mode]);

  const selectedTheme = theme[mode] || theme.light;

  return (
    <ThemeContext.Provider value={{ mode, toggleMode, theme: selectedTheme }}>
      {children}
    </ThemeContext.Provider>
  );
};

export const useTheme = (): ThemeContextType => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error("useTheme must be used within a ThemeProvider");
  }
  return context;
};
