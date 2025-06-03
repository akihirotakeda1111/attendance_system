import { createContext } from "react";

export const AppSettingsContext = createContext({
  inputMaxLength: {
    id: 12,
    password: 12,
    name: 16,
    email: 160,
    minute: 3,
  },
});
