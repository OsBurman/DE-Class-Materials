import { useTasks } from '../contexts/TaskContext'

function FilterBar() {
  const { state, dispatch } = useTasks()
  const { filter } = state

  return (
    <div className="filter-bar">
      <input
        type="text"
        placeholder="Search tasks…"
        value={filter.search}
        onChange={e => dispatch({ type: 'SET_FILTER', payload: { search: e.target.value } })}
      />
      <select
        value={filter.status}
        onChange={e => dispatch({ type: 'SET_FILTER', payload: { status: e.target.value } })}
      >
        <option value="all">All statuses</option>
        <option value="todo">To Do</option>
        <option value="in-progress">In Progress</option>
        <option value="done">Done</option>
      </select>
    </div>
  )
}

export default FilterBar
