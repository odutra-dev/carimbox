import { Fragment } from "react";
import { Slot } from "expo-router";
import { StatusBar } from "expo-status-bar";
import useTheme from "../hooks/useTheme";
export default function Layout() {
  const { colors } = useTheme();
  return (
    <Fragment>
      <StatusBar
        backgroundColor={colors.background}
        style={colors.background === "#F8FAFC" ? "dark" : "light"}
      />
      <Slot />
    </Fragment>
  );
}
