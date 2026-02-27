import { BOOKS } from '../data/books'

// TODO: Show all books in the cart array (array of book ids).
//   Look up each book with BOOKS.find(b => b.id === id).
//   Show the total price at the bottom.
//   If cart is empty, show an empty-cart message with a link to /books.

function CartPage({ cart }) {
  const cartBooks = cart.map(id => BOOKS.find(b => b.id === id)).filter(Boolean)
  const total = cartBooks.reduce((sum, b) => sum + b.price, 0)

  if (cartBooks.length === 0) {
    return (
      <div className="page">
        <div className="empty-cart">
          <p style={{ fontSize: '3rem' }}>🛒</p>
          <p>Your cart is empty.</p>
          <a href="/books" className="btn" style={{ marginTop: '1rem', display: 'inline-block' }}>Browse Books</a>
        </div>
      </div>
    )
  }

  return (
    <div className="page">
      <h1 style={{ marginBottom: '1.5rem' }}>Your Cart</h1>
      {cartBooks.map(book => (
        <div className="cart-item" key={book.id}>
          <span>{book.cover} {book.title}</span>
          <strong>${book.price}</strong>
        </div>
      ))}
      <div style={{ textAlign: 'right', marginTop: '1rem', fontWeight: 700, fontSize: '1.1rem' }}>
        Total: ${total.toFixed(2)}
      </div>
    </div>
  )
}

export default CartPage
