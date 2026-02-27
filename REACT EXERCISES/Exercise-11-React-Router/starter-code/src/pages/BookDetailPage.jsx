import { useParams, Link } from 'react-router-dom'
import { BOOKS } from '../data/books'

// TODO 1: Call useParams() to get `id` from the URL.
// TODO 2: Find the book: const book = BOOKS.find(b => b.id === id)
// TODO 3: If no book found, render a "Book not found" message with a link back to /books.
// TODO 4: Render the book details — cover emoji (large), title, author, genre, price, description.
// TODO 5: Show "✅ Already in cart" if cart.includes(book.id), otherwise an "Add to Cart" button
//   that calls onAddToCart(book.id).

function BookDetailPage({ onAddToCart, cart }) {
  // TODO: implement with useParams
  return (
    <div className="page">
      <Link to="/books" className="back-link">← Back to Books</Link>
      <div className="book-detail">
        <p>Implement this page using useParams()</p>
      </div>
    </div>
  )
}

export default BookDetailPage
