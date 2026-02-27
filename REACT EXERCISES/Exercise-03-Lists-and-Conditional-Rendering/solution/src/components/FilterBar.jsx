export default function FilterBar({ searchTerm, onSearchChange, selectedGenre, onGenreChange, genres }) {
  return (
    <div className="filter-bar">
      <input
        type="text"
        placeholder="🔍 Search movies..."
        value={searchTerm}
        onChange={(e) => onSearchChange(e.target.value)}
      />
      <select
        value={selectedGenre}
        onChange={(e) => onGenreChange(e.target.value)}
      >
        {genres.map(genre => (
          <option key={genre} value={genre}>{genre}</option>
        ))}
      </select>
    </div>
  )
}
