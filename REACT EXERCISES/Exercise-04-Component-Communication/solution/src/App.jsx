import { useState } from 'react'
import './App.css'
import ProductCatalog from './components/ProductCatalog'
import Cart from './components/Cart'

const PRODUCTS = [
  { id: 1, name: 'Wireless Headphones', price: 79.99, emoji: '🎧' },
  { id: 2, name: 'Mechanical Keyboard', price: 129.99, emoji: '⌨️' },
  { id: 3, name: 'USB-C Hub', price: 49.99, emoji: '🔌' },
  { id: 4, name: 'Webcam HD', price: 89.99, emoji: '📷' },
  { id: 5, name: 'Desk Lamp', price: 34.99, emoji: '💡' },
  { id: 6, name: 'Mouse Pad XL', price: 24.99, emoji: '🖱️' },
]

export default function App() {
  const [cartItems, setCartItems] = useState([])

  function addToCart(product) {
    setCartItems(prev => {
      const exists = prev.some(item => item.id === product.id)
      if (exists) {
        return prev.map(item =>
          item.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
        )
      }
      return [...prev, { ...product, quantity: 1 }]
    })
  }

  function removeFromCart(id) {
    setCartItems(prev => prev.filter(item => item.id !== id))
  }

  function updateQuantity(id, newQty) {
    if (newQty < 1) {
      removeFromCart(id)
    } else {
      setCartItems(prev =>
        prev.map(item => item.id === id ? { ...item, quantity: newQty } : item)
      )
    }
  }

  const cartTotal = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const itemCount = cartItems.reduce((sum, item) => sum + item.quantity, 0)

  return (
    <div className="app">
      <header className="app-header">
        <h1>🛒 Tech Store</h1>
        <span className="cart-badge">Cart ({itemCount} items)</span>
      </header>
      <div className="store-layout">
        <ProductCatalog products={PRODUCTS} onAddToCart={addToCart} />
        <Cart
          cartItems={cartItems}
          onRemove={removeFromCart}
          onUpdateQuantity={updateQuantity}
          cartTotal={cartTotal}
          itemCount={itemCount}
        />
      </div>
    </div>
  )
}
