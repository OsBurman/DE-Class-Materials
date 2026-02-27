// TODO 15: Accept `movie`, `onToggle`, `onDelete` as props

export default function MovieCard(props) {
  // const { movie, onToggle, onDelete } = props

  return (
    // TODO 16: Apply the CSS class `movie-card--watched` when movie.watched is true
    //          className={`movie-card ${movie.watched ? 'movie-card--watched' : ''}`}
    <div className="movie-card">

      {/* TODO 17: Show ✅ emoji when watched, empty string otherwise
                  {movie.watched ? '✅' : ''} or use && */}
      <span className="movie-watched-icon"></span>

      {/* TODO: render movie title */}
      <span className="movie-title">Movie Title</span>

      <div className="movie-meta">
        {/* TODO 20: Render genre badge, year, and rating */}
        <span className="genre-badge">Genre</span>
        <span>Year</span>
        <span>⭐ Rating</span>
      </div>

      <div className="movie-actions">
        {/* TODO 18: "Mark Watched" button
                    onClick={() => onToggle(movie.id)}
            TODO 20: Show "✓ Watched" text if movie.watched, else "Mark Watched" */}
        <button className="btn-watch">Mark Watched</button>

        {/* TODO 19: Delete button
                    onClick={() => onDelete(movie.id)} */}
        <button className="btn-delete">🗑</button>
      </div>
    </div>
  )
}
