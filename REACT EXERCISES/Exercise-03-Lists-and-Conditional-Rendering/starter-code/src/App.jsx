// TODO 1: Import useState from 'react'
import './App.css'
import FilterBar from './components/FilterBar'
import MovieList from './components/MovieList'

// ─── Initial movie data ───────────────────────────────────────────────────────
const INITIAL_MOVIES = [
  { id: 1, title: 'Inception', genre: 'Action', year: 2010, rating: 8.8, watched: false },
  { id: 2, title: 'The Grand Budapest Hotel', genre: 'Comedy', year: 2014, rating: 8.1, watched: true },
  { id: 3, title: 'Interstellar', genre: 'Action', year: 2014, rating: 8.6, watched: false },
  { id: 4, title: 'Parasite', genre: 'Drama', year: 2019, rating: 8.5, watched: false },
  { id: 5, title: 'The Dark Knight', genre: 'Action', year: 2008, rating: 9.0, watched: true },
  { id: 6, title: 'Knives Out', genre: 'Comedy', year: 2019, rating: 7.9, watched: false },
  { id: 7, title: 'Marriage Story', genre: 'Drama', year: 2019, rating: 7.9, watched: true },
  { id: 8, title: 'Mad Max: Fury Road', genre: 'Action', year: 2015, rating: 8.1, watched: false },
]

const GENRES = ['All', 'Action', 'Comedy', 'Drama']

export default function App() {
  // TODO 1: Declare `movies` state — initialize with INITIAL_MOVIES
  // TODO 2: Declare `searchTerm` state — initialize to ''
  // TODO 3: Declare `selectedGenre` state — initialize to 'All'

  // TODO 4: Derive `filteredMovies` — do NOT use useState, just compute it:
  //   const filteredMovies = movies
  //     .filter(m => selectedGenre === 'All' || m.genre === selectedGenre)
  //     .filter(m => m.title.toLowerCase().includes(searchTerm.toLowerCase()))
  const filteredMovies = [] // replace with derived value

  // TODO 7: Compute watchedCount — count movies where movie.watched === true
  const watchedCount = 0 // replace with derived value

  // TODO 5: Implement toggleWatched(id)
  //   Find the movie with the matching id and flip its `watched` boolean
  //   Use: movies.map(m => m.id === id ? { ...m, watched: !m.watched } : m)
  function toggleWatched(id) {
    // your code here
  }

  // TODO 6: Implement deleteMovie(id)
  //   Remove the movie with the matching id from the movies array
  //   Use: movies.filter(m => m.id !== id)
  function deleteMovie(id) {
    // your code here
  }

  return (
    <div className="app">
      <header className="app-header">
        <div>
          <h1>🎬 My Watchlist</h1>
          <p className="stats">
            {/* TODO 7: Display watchedCount and movies.length */}
            0 watched / 0 total
          </p>
        </div>
      </header>

      <main className="app-main">
        {/* TODO: Pass the required props to FilterBar */}
        <FilterBar
          searchTerm={''}
          onSearchChange={() => {}}
          selectedGenre={'All'}
          onGenreChange={() => {}}
          genres={GENRES}
        />

        {/* TODO: Pass filteredMovies, toggleWatched, deleteMovie to MovieList */}
        <MovieList
          movies={filteredMovies}
          onToggle={toggleWatched}
          onDelete={deleteMovie}
        />
      </main>
    </div>
  )
}
