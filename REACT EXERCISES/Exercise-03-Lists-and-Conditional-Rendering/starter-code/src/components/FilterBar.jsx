// TODO 8: Accept these props: searchTerm, onSearchChange, selectedGenre, onGenreChange, genres

export default function FilterBar(props) {
  return (
    <div className="filter-bar">
      {/* TODO 9: Controlled search input
                  value={searchTerm}
                  onChange={(e) => onSearchChange(e.target.value)} */}
      <input
        type="text"
        placeholder="🔍 Search movies..."
      />

      {/* TODO 10: Controlled genre select
                   value={selectedGenre}
                   onChange={(e) => onGenreChange(e.target.value)} */}
      <select>
        {/* TODO 11: Map over the `genres` array to render <option> elements.
                     Each option's value and text content should be the genre string.
                     Remember to add a key prop! */}
        <option>All</option>
      </select>
    </div>
  )
}
