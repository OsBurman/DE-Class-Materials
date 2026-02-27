import { useParams, Link } from 'react-router-dom'
import { BOOKS } from '../data/books'

function BookDetailPage({ onAddToCart, cart }) {
  const { id } = useParams()
  const book = BOOKS.find(b => b.id === id)

  if (!book) {
    return (
      <div className="page">
        <Link to="/books" className="back-link">← Back to Books</Link>
        <p>Book not found.</p>
      </div>
    )
  }

  const inCart = cart.includes(book.id)

  return (
    <div className="page">
      <Link to="/books" className="back-link">← Back to Books</Link>
      <div className="book-detail">
        <div className="cover-large">{book.cover}</div>
        <h1>{book.title}</h1>
        <div className="meta">
          by {book.author} · <span className="book-genre-badge">{book.genre}</span>
        </div>
        <p className="description">{book.description}</p>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <span style={{ fontSize: '1.5rem', fontWeight: 700, color: '#6366f1' }}>${book.price}</span>
          <button
            className="btn"
            onClick={() => onAddToCart(book.id)}
            disabled={inCart}
            style={{ opacity: inCart ? 0.6 : 1, cursor: inCart ? 'not-allowed' : 'pointer' }}
          >
            {inCart ? '✓ In Cart' : 'Add to Cart'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default BookDetailPage
