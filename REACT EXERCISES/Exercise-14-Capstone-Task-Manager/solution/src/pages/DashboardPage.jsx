import { useMemo } from 'react'
import { Link } from 'react-router-dom'
import { useTasks } from '../contexts/TaskContext'
import { isOverdue } from '../utils/dateUtils'

function DashboardPage() {
  const { state } = useTasks()

  const stats = useMemo(() => ({
    total: state.tasks.length,
    todo: state.tasks.filter(t => t.status === 'todo').length,
    inProgress: state.tasks.filter(t => t.status === 'in-progress').length,
    done: state.tasks.filter(t => t.status === 'done').length,
    overdue: state.tasks.filter(t => isOverdue(t.dueDate) && t.status !== 'done').length,
  }), [state.tasks])

  return (
    <div>
      <div className="page-header">
        <h1>Dashboard</h1>
        <Link to="/tasks/new" className="btn">+ New Task</Link>
      </div>

      <div className="stats-grid">
        <div className="stat-card"><div className="stat-value">{stats.total}</div><div className="stat-label">Total Tasks</div></div>
        <div className="stat-card"><div className="stat-value" style={{ color: '#f59e0b' }}>{stats.todo}</div><div className="stat-label">To Do</div></div>
        <div className="stat-card"><div className="stat-value" style={{ color: '#3b82f6' }}>{stats.inProgress}</div><div className="stat-label">In Progress</div></div>
        <div className="stat-card"><div className="stat-value" style={{ color: '#22c55e' }}>{stats.done}</div><div className="stat-label">Done</div></div>
        <div className="stat-card"><div className="stat-value" style={{ color: '#ef4444' }}>{stats.overdue}</div><div className="stat-label">Overdue</div></div>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
        <h2>Recent Tasks</h2>
        <Link to="/tasks" style={{ color: 'var(--accent)', fontSize: '0.9rem' }}>View all →</Link>
      </div>
      <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
        Navigate to the Tasks page to manage all your tasks.
      </p>
    </div>
  )
}

export default DashboardPage
