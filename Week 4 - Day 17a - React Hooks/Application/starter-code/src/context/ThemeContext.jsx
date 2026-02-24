import { createContext, useState, useContext } from 'react';

// TODO Task 2: Create the ThemeContext with a default value
// Default: { theme: 'light', toggleTheme: () => {} }
const ThemeContext = createContext(/* TODO */);

// TODO Task 2: Build the ThemeProvider component
// - Use useState to manage theme ('light' | 'dark')
// - toggleTheme: flip between 'light' and 'dark'
// - Return <ThemeContext.Provider value={{ theme, toggleTheme }}>
export function ThemeProvider({ children }) {
  // TODO: implement
  return <>{children}</>;
}

// TODO Task 2: Export a custom hook useTheme
// It should call useContext(ThemeContext)
export function useTheme() {
  // TODO: return useContext(ThemeContext)
  return { theme: 'light', toggleTheme: () => {} };
}
