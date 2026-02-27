import { useReducer } from 'react'
import { tasksReducer, INITIAL_STATE } from '../reducers/tasksReducer'
import KanbanColumn from './KanbanColumn'
import AddTaskForm from './AddTaskForm'
import FilterBar from './FilterBar'

const COLUMNS = [
  { key: 'todo', label: '📝 To Do' },
  { key: 'in-progress', label: '🔄 In Progress' },
  { key: 'done', label: '✅ Done' },
]

// TODO 1: Call useReducer with tasksReducer and INITIAL_STATE.
//   Destructure [state, dispatch] from the result.

// TODO 2: Compute `filteredTasks`:
//   - If state.filter === 'all', use all tasks.
//   - Otherwise filter to tasks whose priority matches state.filter.

// TODO 3: For each column, pass dispatch and the filteredTasks for that column's status.

function KanbanBoard() {
  // TODO: implement

  return (
    <div className="kanban">
      <div className="kanban-controls">
        {/* TODO 4: Pass dispatch to AddTaskForm and FilterBar */}
        <AddTaskForm dispatch={() => {}} />
        <FilterBar currentFilter="all" dispatch={() => {}} />
      </div>
      <div className="kanban-columns">
        {COLUMNS.map(col => (
          <KanbanColumn
            key={col.key}
            label={col.label}
            tasks={[]}
            dispatch={() => {}}
          />
        ))}
      </div>
    </div>
  )
}

export default KanbanBoard
