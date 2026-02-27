import { Link } from 'react-router-dom'
import { BOOKS } from '../data/books'

// TODO: Render a grid of book cards, each linking to /books/:id using <Link to={`/books/${book.id}`}>

function BooksPage() {
  return (
    <div className="page">
      <h1 style={{ marginBottom: '1.5rem' }}>All Books</h1>
      <div className="books-grid">
        {BOOKS.map(book => (
          <Link to={`/books/${book.id}`} className="book-card" key={book.id}>
            <div className="book-cover">{book.cover}</div>
            <div className="book-title">{book.title}</div>
            <div className="book-author">{book.author}</div>
            <div className="book-price">${book.price}</div>
            <div style={{ marginTop: '0.4rem' }}>
              <span className="book-genre-badge">{book.genre}</span>
            </div>
          </Link>
        ))}
      </div>
    </div>
  )
}

export default BooksPage
