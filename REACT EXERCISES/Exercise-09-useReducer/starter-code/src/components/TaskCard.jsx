import { useState } from 'react'

const STATUSES = ['todo', 'in-progress', 'done']
const PRIORITY_COLORS = { High: '#ef4444', Medium: '#f59e0b', Low: '#22c55e' }

// TODO 1: Add `isEditing` state (boolean) and `editTitle` state (string).
//   When edit mode starts, initialise editTitle to task.title.

// TODO 2: Dispatch MOVE_TASK:
//   dispatch({ type: 'MOVE_TASK', payload: { id: task.id, direction: +1 or -1 } })
//   Disable the forward button when the task is already 'done'.
//   Disable the backward button when the task is already 'todo'.

// TODO 3: Dispatch DELETE_TASK:
//   dispatch({ type: 'DELETE_TASK', payload: { id: task.id } })

// TODO 4: Dispatch EDIT_TASK on save:
//   dispatch({ type: 'EDIT_TASK', payload: { id: task.id, title: editTitle } })
//   Then set isEditing to false.

function TaskCard({ task, dispatch }) {
  // TODO: add isEditing and editTitle state

  const statusIndex = STATUSES.indexOf(task.status)

  return (
    <div className="task-card">
      <div className="task-top">
        {/* TODO 5: When isEditing, render an <input> with editTitle + onChange.
              When not editing, render a <span> with task.title. */}
        <span className="task-title">{task.title}</span>
        <span className="priority-badge" style={{ background: PRIORITY_COLORS[task.priority] }}>
          {task.priority}
        </span>
      </div>
      <div className="task-actions">
        {/* TODO 6: back button — dispatch MOVE_TASK direction -1, disabled when statusIndex === 0 */}
        <button className="btn-move" disabled>← Back</button>
        {/* TODO 7: edit/save toggle */}
        <button className="btn-edit">✏️</button>
        {/* TODO 8: delete button */}
        <button className="btn-delete">🗑️</button>
        {/* TODO 9: forward button — dispatch MOVE_TASK direction +1, disabled when statusIndex === 2 */}
        <button className="btn-move" disabled>Forward →</button>
      </div>
    </div>
  )
}

export default TaskCard
