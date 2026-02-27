import { useTasks } from '../contexts/TaskContext'

function FilterBar() {
  const { state, dispatch } = useTasks()
  const { filter } = state

  function handleStatusChange(e) {
    dispatch({ type: 'SET_FILTER', payload: { status: e.target.value } })
  }

  function handleSearchChange(e) {
    dispatch({ type: 'SET_FILTER', payload: { search: e.target.value } })
  }

  return (
    <div className="filter-bar">
      <input
        type="text"
        placeholder="Search tasks…"
        value={filter.search}
        onChange={handleSearchChange}
      />
      <select value={filter.status} onChange={handleStatusChange}>
        <option value="all">All statuses</option>
        <option value="todo">To Do</option>
        <option value="in-progress">In Progress</option>
        <option value="done">Done</option>
      </select>
    </div>
  )
}

export default FilterBar
