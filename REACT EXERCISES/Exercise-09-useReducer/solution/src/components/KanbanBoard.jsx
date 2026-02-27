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

function KanbanBoard() {
  const [state, dispatch] = useReducer(tasksReducer, INITIAL_STATE)
  const filteredTasks = state.filter === 'all' ? state.tasks : state.tasks.filter(t => t.priority === state.filter)

  return (
    <div className="kanban">
      <div className="kanban-controls">
        <AddTaskForm dispatch={dispatch} />
        <FilterBar currentFilter={state.filter} dispatch={dispatch} />
      </div>
      <div className="kanban-columns">
        {COLUMNS.map(col => (
          <KanbanColumn
            key={col.key}
            label={col.label}
            tasks={filteredTasks.filter(t => t.status === col.key)}
            dispatch={dispatch}
          />
        ))}
      </div>
    </div>
  )
}

export default KanbanBoard
