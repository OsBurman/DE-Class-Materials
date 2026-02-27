import MovieCard from './MovieCard'

// TODO 12: Accept `movies`, `onToggle`, `onDelete` as props

export default function MovieList(props) {
  return (
    <div className="movie-list">
      {/* TODO 13: If movies.length === 0, show an empty state message.
                  Use a ternary:
                  {movies.length === 0
                    ? <p className="empty-state">No movies match your search 🎭</p>
                    : movies.map(...) }

          TODO 14: If there are movies, map over them and render:
                  <MovieCard
                    key={movie.id}
                    movie={movie}
                    onToggle={onToggle}
                    onDelete={onDelete}
                  /> */}
      <p className="empty-state">Implement the movie list here 🎬</p>
    </div>
  )
}
