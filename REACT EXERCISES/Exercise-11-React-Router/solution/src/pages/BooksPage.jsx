import { Link } from 'react-router-dom'
import { BOOKS } from '../data/books'

const GENRES = ['All', ...new Set(BOOKS.map(b => b.genre))]

function BooksPage() {
  return (
    <div className="page">
      <h1>All Books</h1>
      <p style={{ color: '#64748b', marginBottom: '1.5rem' }}>{BOOKS.length} books available</p>

      <div className="books-grid">
        {BOOKS.map(book => (
          <Link key={book.id} to={`/books/${book.id}`} className="book-card">
            <div className="book-cover">{book.cover}</div>
            <div className="book-title">{book.title}</div>
            <div className="book-author">{book.author}</div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '0.5rem' }}>
              <span className="book-price">${book.price}</span>
              <span className="book-genre-badge">{book.genre}</span>
            </div>
          </Link>
        ))}
      </div>
    </div>
  )
}

export default BooksPage
