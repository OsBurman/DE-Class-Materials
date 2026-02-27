import { Link, useParams } from 'react-router-dom'
import { useTasks } from '../contexts/TaskContext'
import { formatDate, isOverdue } from '../utils/dateUtils'

// TODO 8: Use useParams to get `id` from the URL.
// Find the task in state.tasks where task.id === Number(id).
// If no task is found, render a "Task not found" message with a link back to /tasks.

const STATUS_LABEL = { todo: 'To Do', 'in-progress': 'In Progress', done: 'Done' }
const STATUS_BADGE = { todo: 'badge-todo', 'in-progress': 'badge-in-progress', done: 'badge-done' }
const PRIORITY_BADGE = { High: 'badge-high', Medium: 'badge-medium', Low: 'badge-low' }

function TaskDetailPage() {
  const { state } = useTasks()
  // TODO 8: Replace null with the correct useParams + find logic
  const task = null

  if (!task) {
    return (
      <div>
        <p>Task not found. <Link to="/tasks" style={{ color: 'var(--accent)' }}>Back to Tasks</Link></p>
      </div>
    )
  }

  return (
    <div>
      <Link to="/tasks" style={{ color: 'var(--accent)', fontSize: '0.9rem', display: 'inline-block', marginBottom: '1.5rem' }}>← Back to Tasks</Link>
      <div className="card" style={{ maxWidth: 640 }}>
        <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem', flexWrap: 'wrap' }}>
          <span className={`badge ${STATUS_BADGE[task.status]}`}>{STATUS_LABEL[task.status]}</span>
          <span className={`badge ${PRIORITY_BADGE[task.priority]}`}>{task.priority} Priority</span>
          {task.dueDate && isOverdue(task.dueDate) && task.status !== 'done' && (
            <span className="badge" style={{ background: '#fee2e2', color: '#991b1b' }}>⚠️ Overdue</span>
          )}
        </div>
        <h1 style={{ marginBottom: '0.75rem' }}>{task.title}</h1>
        {task.description && <p style={{ color: 'var(--text-muted)', lineHeight: 1.7, marginBottom: '1rem' }}>{task.description}</p>}
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Due: {formatDate(task.dueDate)}</p>
        <div style={{ marginTop: '1.5rem' }}>
          <Link to={`/tasks/${task.id}/edit`} className="btn">Edit Task</Link>
        </div>
      </div>
    </div>
  )
}

export default TaskDetailPage
