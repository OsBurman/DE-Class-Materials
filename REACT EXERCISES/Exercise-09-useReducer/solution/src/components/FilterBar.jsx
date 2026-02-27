const FILTERS = ['all', 'High', 'Medium', 'Low']

function FilterBar({ currentFilter, dispatch }) {
  return (
    <div className="filter-bar">
      <span>Filter by priority:</span>
      {FILTERS.map(f => (
        <button key={f} className={f === currentFilter ? 'active' : ''}
          onClick={() => dispatch({ type: 'SET_FILTER', payload: { filter: f } })}>
          {f === 'all' ? 'All' : f}
        </button>
      ))}
    </div>
  )
}

export default FilterBar
