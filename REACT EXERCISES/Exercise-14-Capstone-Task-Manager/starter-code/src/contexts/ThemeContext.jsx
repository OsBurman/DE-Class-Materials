import { createContext, useContext } from 'react'
import useLocalStorage from '../hooks/useLocalStorage'

const ThemeContext = createContext(null)

export function ThemeProvider({ children }) {
  // TODO 2: Use useLocalStorage to persist the theme ('light' | 'dark').
  // Implement toggleTheme() to switch between the two values.
  const [theme, setTheme] = useLocalStorage('tm-theme', 'light')

  function toggleTheme() {
    // Replace this with the toggle logic
  }

  return (
    <ThemeContext.Provider value={{ theme, toggleTheme }}>
      <div data-theme={theme} style={{ minHeight: '100vh' }}>
        {children}
      </div>
    </ThemeContext.Provider>
  )
}

export function useTheme() {
  const ctx = useContext(ThemeContext)
  if (!ctx) throw new Error('useTheme must be used inside ThemeProvider')
  return ctx
}
