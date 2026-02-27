import MovieCard from './MovieCard'

export default function MovieList({ movies, onToggle, onDelete }) {
  return (
    <div className="movie-list">
      {movies.length === 0
        ? <p className="empty-state">No movies match your search 🎭</p>
        : movies.map(movie => (
            <MovieCard
              key={movie.id}
              movie={movie}
              onToggle={onToggle}
              onDelete={onDelete}
            />
          ))
      }
    </div>
  )
}
