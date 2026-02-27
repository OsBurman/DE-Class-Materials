import { useState } from 'react'

const PRIORITIES = ['High', 'Medium', 'Low']

function AddTaskForm({ dispatch }) {
  const [title, setTitle] = useState('')
  const [priority, setPriority] = useState('Medium')

  function handleSubmit(e) {
    e.preventDefault()
    if (!title.trim()) return
    dispatch({ type: 'ADD_TASK', payload: { title: title.trim(), priority } })
    setTitle('')
    setPriority('Medium')
  }

  return (
    <form className="add-task-form" onSubmit={handleSubmit}>
      <input type="text" placeholder="New task title…" value={title} onChange={e => setTitle(e.target.value)} />
      <select value={priority} onChange={e => setPriority(e.target.value)}>
        {PRIORITIES.map(p => <option key={p} value={p}>{p}</option>)}
      </select>
      <button type="submit" className="btn-add">+ Add Task</button>
    </form>
  )
}

export default AddTaskForm
