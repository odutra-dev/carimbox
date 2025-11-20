import { I18n } from "i18n-js";
import { enLang } from "./en";
import { ptLang } from "./pt";

const locales = {
  en: enLang,
  pt: ptLang,
};

export const i18n = new I18n(locales);

export const changeLanguage = (locale: string) => (i18n.locale = locale);
