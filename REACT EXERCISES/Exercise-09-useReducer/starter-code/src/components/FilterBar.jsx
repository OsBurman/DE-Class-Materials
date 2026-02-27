const FILTERS = ['all', 'High', 'Medium', 'Low']

// TODO 1: For each filter in FILTERS, render a button.
//   Apply className="active" when the filter equals currentFilter.
//   On click dispatch: dispatch({ type: 'SET_FILTER', payload: { filter } })

function FilterBar({ currentFilter, dispatch }) {
  return (
    <div className="filter-bar">
      <span>Filter by priority:</span>
      {/* TODO: render filter buttons */}
      {FILTERS.map(f => (
        <button key={f} className={f === currentFilter ? 'active' : ''} onClick={() => {}}>
          {f === 'all' ? 'All' : f}
        </button>
      ))}
    </div>
  )
}

export default FilterBar
