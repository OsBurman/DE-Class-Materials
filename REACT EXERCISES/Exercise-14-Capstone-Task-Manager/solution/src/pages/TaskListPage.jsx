import { useMemo } from 'react'
import { Link } from 'react-router-dom'
import { useTasks } from '../contexts/TaskContext'
import FilterBar from '../components/FilterBar'
import TaskCard from '../components/TaskCard'

function TaskListPage() {
  const { state } = useTasks()

  const filteredTasks = useMemo(() => {
    return state.tasks.filter(task => {
      const matchesStatus = state.filter.status === 'all' || task.status === state.filter.status
      const matchesSearch = task.title.toLowerCase().includes(state.filter.search.toLowerCase())
      return matchesStatus && matchesSearch
    })
  }, [state.tasks, state.filter])

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
