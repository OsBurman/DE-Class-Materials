import { Link } from 'react-router-dom'
import { useTasks } from '../contexts/TaskContext'
import { formatDate, isOverdue } from '../utils/dateUtils'

const PRIORITY_BADGE = { High: 'badge-high', Medium: 'badge-medium', Low: 'badge-low' }
const STATUS_BADGE = { todo: 'badge-todo', 'in-progress': 'badge-in-progress', done: 'badge-done' }
const STATUS_LABEL = { todo: 'To Do', 'in-progress': 'In Progress', done: 'Done' }

function TaskCard({ task }) {
  const { dispatch } = useTasks()

  function handleDelete() {
    if (window.confirm(`Delete "${task.title}"?`)) {
      dispatch({ type: 'DELETE_TASK', payload: task.id })
    }
  }

  return (
    <div className="task-item">
      <div style={{ flex: 1 }}>
        <Link to={`/tasks/${task.id}`} className="task-title-link">{task.title}</Link>
        <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.4rem', flexWrap: 'wrap' }}>
          <span className={`badge ${STATUS_BADGE[task.status]}`}>{STATUS_LABEL[task.status]}</span>
          <span className={`badge ${PRIORITY_BADGE[task.priority]}`}>{task.priority}</span>
          {task.dueDate && (
            <span style={{ fontSize: '0.78rem', color: isOverdue(task.dueDate) && task.status !== 'done' ? '#ef4444' : 'var(--text-muted)' }}>
              📅 {formatDate(task.dueDate)}
              {isOverdue(task.dueDate) && task.status !== 'done' && ' ⚠️ Overdue'}
            </span>
          )}
        </div>
      </div>
      <div style={{ display: 'flex', gap: '0.5rem' }}>
        <Link to={`/tasks/${task.id}/edit`} className="btn btn-outline btn-sm">Edit</Link>
        <button className="btn btn-danger btn-sm" onClick={handleDelete}>Delete</button>
      </div>
    </div>
  )
}

export default TaskCard
