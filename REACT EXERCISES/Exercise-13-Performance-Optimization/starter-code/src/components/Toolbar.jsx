function Toolbar({ search, onSearchChange, category, onCategoryChange, minPrice, onMinPriceChange, categories, resultCount }) {
  return (
    <div className="toolbar">
      <input
        type="text"
        placeholder="Search products…"
        value={search}
        onChange={e => onSearchChange(e.target.value)}
      />

      <select value={category} onChange={e => onCategoryChange(e.target.value)}>
        {categories.map(c => <option key={c}>{c}</option>)}
      </select>

      <label>
        Min price: ${minPrice}
        <input
          type="range"
          min="0"
          max="500"
          step="10"
          value={minPrice}
          onChange={e => onMinPriceChange(Number(e.target.value))}
        />
      </label>

      <span className="result-count">{resultCount} products</span>
    </div>
  )
}

export default Toolbar
