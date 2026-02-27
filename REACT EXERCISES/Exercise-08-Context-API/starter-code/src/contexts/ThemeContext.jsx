import { createContext, useState, useContext } from 'react'

// TODO 1: Create ThemeContext using createContext().
//   Export it so components can import and use it with useContext().

// TODO 2: Build and export ThemeProvider({ children }):
//   - Add `theme` state initialised to 'light'.
//   - Implement toggleTheme() that switches between 'light' and 'dark'.
//   - Return <ThemeContext.Provider value={{ theme, toggleTheme }}>{children}</ThemeContext.Provider>

// TODO 3 (Bonus): Export a custom hook `useTheme()` that calls useContext(ThemeContext)
//   and throws an error if used outside a ThemeProvider.
//   Pattern:
//     export function useTheme() {
//       const ctx = useContext(ThemeContext)
//       if (!ctx) throw new Error('useTheme must be used inside ThemeProvider')
//       return ctx
//     }

export const ThemeContext = createContext(null)

export function ThemeProvider({ children }) {
  // TODO: implement
  return <>{children}</>
}
