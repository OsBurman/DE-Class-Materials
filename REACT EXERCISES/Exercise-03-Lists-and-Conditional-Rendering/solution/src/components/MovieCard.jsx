export default function MovieCard({ movie, onToggle, onDelete }) {
  return (
    <div className={`movie-card ${movie.watched ? 'movie-card--watched' : ''}`}>
      {/* Conditionally show the watched icon */}
      <span className="movie-watched-icon">{movie.watched ? '✅' : ''}</span>

      <span className="movie-title">{movie.title}</span>

      <div className="movie-meta">
        <span className="genre-badge">{movie.genre}</span>
        <span>{movie.year}</span>
        <span>⭐ {movie.rating}</span>
      </div>

      <div className="movie-actions">
        <button className="btn-watch" onClick={() => onToggle(movie.id)}>
          {movie.watched ? '↩ Unwatch' : '✓ Mark Watched'}
        </button>
        <button className="btn-delete" onClick={() => onDelete(movie.id)}>🗑</button>
      </div>
    </div>
  )
}
