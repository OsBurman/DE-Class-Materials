import { useState } from 'react'
import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import HomePage from './pages/HomePage'
import BooksPage from './pages/BooksPage'
import BookDetailPage from './pages/BookDetailPage'
import CartPage from './pages/CartPage'
import LoginPage from './pages/LoginPage'
import ProfilePage from './pages/ProfilePage'
import NotFoundPage from './pages/NotFoundPage'
import './App.css'

function App() {
  const [user, setUser] = useState(null)
  const [cart, setCart] = useState([])

  function handleAddToCart(bookId) {
    setCart(prev => prev.includes(bookId) ? prev : [...prev, bookId])
  }

  function handleLogin(name) { setUser({ name }) }
  function handleLogout() { setUser(null) }

  return (
    <Routes>
      <Route element={<Layout user={user} cart={cart} onLogout={handleLogout} />}>
        <Route index element={<HomePage />} />
        <Route path="books" element={<BooksPage />} />
        <Route path="books/:id" element={<BookDetailPage onAddToCart={handleAddToCart} cart={cart} />} />
        <Route path="cart" element={<CartPage cart={cart} />} />
        <Route path="login" element={<LoginPage onLogin={handleLogin} />} />
        <Route path="profile" element={
          <ProtectedRoute user={user}><ProfilePage user={user} /></ProtectedRoute>
        } />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}

export default App
