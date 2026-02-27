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

// TODO 1: Add `user` state (initialised to null) and `cart` state (initialised to []).

// TODO 2: Implement handleAddToCart(bookId):
//   Only add if the id is not already in the cart.

// TODO 3: Implement handleLogin(name): set user to { name }.
// TODO 4: Implement handleLogout(): set user to null, optionally clear the cart.

// TODO 5: Define all routes inside <Routes>:
//   Use a parent <Route element={<Layout user={user} cart={cart} onLogout={handleLogout} />}>
//   and nest all page routes inside it.
//
//   Routes to define:
//     index → <HomePage />
//     /books → <BooksPage />
//     /books/:id → <BookDetailPage onAddToCart={handleAddToCart} cart={cart} />
//     /cart → <CartPage cart={cart} />
//     /login → <LoginPage onLogin={handleLogin} />
//     /profile → <ProtectedRoute user={user}><ProfilePage user={user} /></ProtectedRoute>
//     * → <NotFoundPage />

function App() {
  // TODO: add state and handlers

  return (
    <Routes>
      {/* TODO: add your routes here */}
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}

export default App
