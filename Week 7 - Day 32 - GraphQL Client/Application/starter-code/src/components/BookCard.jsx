/**
 * BookCard — displays a summary card for a single book.
 *
 * TODO Task 6a: Complete this component.
 * Display: title, author name, genre, published year, average rating (if available)
 */
export default function BookCard({ book, onClick }) {
  return (
    <div className="book-card" onClick={onClick}>
      {/* TODO: Display book title */}
      <h3>{book.title}</h3>

      {/* TODO: Display genre */}
      <p className="genre">{book.genre} · {book.publishedYear}</p>

      {/* TODO: Display author name (book.author.name) */}
      <p style={{ fontSize: '0.9rem', marginBottom: '0.5rem' }}>
        by {book.author?.name ?? 'Unknown'}
      </p>

      {/* TODO: Display averageRating if available — show "No ratings yet" otherwise */}
      {book.averageRating != null
        ? <p className="rating">⭐ {book.averageRating.toFixed(1)}</p>
        : <p style={{ color: '#9ca3af', fontSize: '0.85rem' }}>No ratings yet</p>
      }
    </div>
  )
}
