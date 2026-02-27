import { useContext } from 'react'
import { ThemeContext, ThemeProvider } from './contexts/ThemeContext'
import { UserProvider } from './contexts/UserContext'
import Header from './components/Header'
import Sidebar from './components/Sidebar'
import BlogList from './components/BlogList'
import './App.css'

const SAMPLE_POSTS = [
  { id: 1, title: 'Getting Started with React Context', author: 'Alice', date: 'June 1, 2025', excerpt: 'Context API lets you share values across the entire component tree without prop drilling. In this post we explore the basics.' },
  { id: 2, title: 'Why useContext is Better Than Redux for Small Apps', author: 'Bob', date: 'June 5, 2025', excerpt: 'For many applications, the built-in Context API combined with useReducer is all you need. Let\'s see when to reach for each.' },
  { id: 3, title: 'Theming with CSS Custom Properties and React', author: 'Carol', date: 'June 10, 2025', excerpt: 'CSS custom properties (variables) are a perfect partner for React state-driven theming. Zero extra libraries required.' },
]

function AppContent() {
  const { theme } = useContext(ThemeContext)
  return (
    <div className="app-wrapper" data-theme={theme}>
      <Header />
      <div className="app-body">
        <Sidebar />
        <main className="main-content">
          <BlogList posts={SAMPLE_POSTS} />
        </main>
      </div>
    </div>
  )
}

function App() {
  return (
    <ThemeProvider>
      <UserProvider>
        <AppContent />
      </UserProvider>
    </ThemeProvider>
  )
}

export default App
