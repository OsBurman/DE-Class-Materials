import { useState } from 'react'
import './App.css'
import FilterBar from './components/FilterBar'
import MovieList from './components/MovieList'

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
  const [movies, setMovies] = useState(INITIAL_MOVIES)
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedGenre, setSelectedGenre] = useState('All')

  // Derived state — computed from existing state, no useState needed
  const filteredMovies = movies
    .filter(m => selectedGenre === 'All' || m.genre === selectedGenre)
    .filter(m => m.title.toLowerCase().includes(searchTerm.toLowerCase()))

  // Also derived
  const watchedCount = movies.filter(m => m.watched).length

  function toggleWatched(id) {
    setMovies(prev =>
      prev.map(m => m.id === id ? { ...m, watched: !m.watched } : m)
    )
  }

  function deleteMovie(id) {
    setMovies(prev => prev.filter(m => m.id !== id))
  }

  return (
    <div className="app">
      <header className="app-header">
        <div>
          <h1>🎬 My Watchlist</h1>
          <p className="stats">{watchedCount} watched / {movies.length} total</p>
        </div>
      </header>

      <main className="app-main">
        <FilterBar
          searchTerm={searchTerm}
          onSearchChange={setSearchTerm}
          selectedGenre={selectedGenre}
          onGenreChange={setSelectedGenre}
          genres={GENRES}
        />
        <MovieList
          movies={filteredMovies}
          onToggle={toggleWatched}
          onDelete={deleteMovie}
        />
      </main>
    </div>
  )
}
