import { Link } from 'react-router-dom'
import { BOOKS } from '../data/books'

function CartPage({ cart }) {
  const cartBooks = BOOKS.filter(b => cart.includes(b.id))
  const total = cartBooks.reduce((sum, b) => sum + b.price, 0)

  if (cartBooks.length === 0) {
    return (
      <div className="page">
        <h1>Your Cart</h1>
        <div className="empty-cart">
          <p style={{ fontSize: '3rem' }}>🛒</p>
          <p>Your cart is empty.</p>
          <Link to="/books" className="btn" style={{ marginTop: '1rem' }}>Browse Books</Link>
        </div>
      </div>
    )
  }

  return (
    <div className="page">
      <h1>Your Cart</h1>
      {cartBooks.map(book => (
        <div key={book.id} className="cart-item">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <span style={{ fontSize: '1.5rem' }}>{book.cover}</span>
            <div>
              <div style={{ fontWeight: 600 }}>{book.title}</div>
              <div style={{ fontSize: '0.85rem', color: '#64748b' }}>{book.author}</div>
            </div>
          </div>
          <span style={{ fontWeight: 700, color: '#6366f1' }}>${book.price}</span>
        </div>
      ))}
      <div style={{ textAlign: 'right', marginTop: '1.5rem', fontSize: '1.2rem', fontWeight: 700 }}>
        Total: ${total.toFixed(2)}
      </div>
      <div style={{ textAlign: 'right', marginTop: '1rem' }}>
        <button className="btn" onClick={() => alert('Order placed! (demo)')}>Checkout</button>
      </div>
    </div>
  )
}

export default CartPage
