import { useState } from 'react'

const STATUSES = ['todo', 'in-progress', 'done']
const PRIORITY_COLORS = { High: '#ef4444', Medium: '#f59e0b', Low: '#22c55e' }

function TaskCard({ task, dispatch }) {
  const [isEditing, setIsEditing] = useState(false)
  const [editTitle, setEditTitle] = useState(task.title)
  const statusIndex = STATUSES.indexOf(task.status)

  function handleSave() {
    if (editTitle.trim()) {
      dispatch({ type: 'EDIT_TASK', payload: { id: task.id, title: editTitle.trim() } })
    }
    setIsEditing(false)
  }

  return (
    <div className="task-card">
      <div className="task-top">
        {isEditing
          ? <input className="task-title-input" value={editTitle} onChange={e => setEditTitle(e.target.value)} autoFocus />
          : <span className="task-title">{task.title}</span>
        }
        <span className="priority-badge" style={{ background: PRIORITY_COLORS[task.priority] }}>{task.priority}</span>
      </div>
      <div className="task-actions">
        <button className="btn-move" disabled={statusIndex === 0}
          onClick={() => dispatch({ type: 'MOVE_TASK', payload: { id: task.id, direction: -1 } })}>← Back</button>
        {isEditing
          ? <button className="btn-save" onClick={handleSave}>💾 Save</button>
          : <button className="btn-edit" onClick={() => { setEditTitle(task.title); setIsEditing(true) }}>✏️</button>
        }
        <button className="btn-delete" onClick={() => dispatch({ type: 'DELETE_TASK', payload: { id: task.id } })}>🗑️</button>
        <button className="btn-move" disabled={statusIndex === 2}
          onClick={() => dispatch({ type: 'MOVE_TASK', payload: { id: task.id, direction: +1 } })}>Forward →</button>
      </div>
    </div>
  )
}

export default TaskCard
