import { useMemo } from 'react'
import { Link } from 'react-router-dom'
import { useTasks } from '../contexts/TaskContext'
import FilterBar from '../components/FilterBar'
import TaskCard from '../components/TaskCard'

// TODO 7: Compute filteredTasks using useMemo.
// Filter state.tasks by:
//   - filter.status: if not 'all', only show tasks where task.status === filter.status
//   - filter.search: if not empty, only show tasks where task.title includes the search string (case-insensitive)

function TaskListPage() {
  const { state } = useTasks()

  // Replace with useMemo:
  const filteredTasks = state.tasks

  return (
    <div>
      <div className="page-header">
        <h1>Tasks</h1>
        <Link to="/tasks/new" className="btn">+ New Task</Link>
      </div>

      <FilterBar />

      {filteredTasks.length === 0 ? (
        <div className="empty-state">
          <div className="icon">📭</div>
          <p>No tasks match your filters.</p>
        </div>
      ) : (
        <div className="task-list">
          {filteredTasks.map(task => (
            <TaskCard key={task.id} task={task} />
          ))}
        </div>
      )}
    </div>
  )
}

export default TaskListPage
