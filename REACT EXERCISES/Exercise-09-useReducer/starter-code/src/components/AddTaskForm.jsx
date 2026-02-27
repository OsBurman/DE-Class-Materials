import { useState } from 'react'

const PRIORITIES = ['High', 'Medium', 'Low']

// TODO 1: Add state: title (string, '') and priority (string, 'Medium').

// TODO 2: On form submit:
//   - Prevent default.
//   - If title.trim() is empty, return early.
//   - Dispatch: dispatch({ type: 'ADD_TASK', payload: { title: title.trim(), priority } })
//   - Reset: setTitle(''), setPriority('Medium').

function AddTaskForm({ dispatch }) {
  // TODO: add state here

  return (
    <form className="add-task-form" onSubmit={() => {}}>
      {/* TODO 3: Bind value and onChange for the title input */}
      <input type="text" placeholder="New task title…" />
      {/* TODO 4: Bind value and onChange for the priority select */}
      <select>
        {PRIORITIES.map(p => <option key={p} value={p}>{p}</option>)}
      </select>
      <button type="submit" className="btn-add">+ Add Task</button>
    </form>
  )
}

export default AddTaskForm
