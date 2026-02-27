import { useState, useRef } from 'react'

const EMPTY = { title: '', description: '', priority: 'Medium', dueDate: '', status: 'todo' }

function TaskForm({ initialData = EMPTY, onSubmit, submitLabel = 'Save Task', clearOnSubmit = false }) {
  const [form, setForm] = useState({ ...EMPTY, ...initialData })
  const [errors, setErrors] = useState({})
  const titleRef = useRef(null)

  function handleChange(e) {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
    setErrors(prev => ({ ...prev, [name]: '' }))
  }

  function handleSubmit(e) {
    e.preventDefault()
    const newErrors = {}
    if (!form.title.trim()) newErrors.title = 'Title is required.'
    if (!form.dueDate) newErrors.dueDate = 'Due date is required.'
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors)
      titleRef.current?.focus()
      return
    }
    onSubmit(form)
    if (clearOnSubmit) setForm(EMPTY)
  }

  return (
    <form onSubmit={handleSubmit} noValidate>
      <div className="form-group">
        <label htmlFor="title">Title *</label>
        <input id="title" name="title" ref={titleRef} value={form.title} onChange={handleChange}
          className={errors.title ? 'input-error' : ''} placeholder="What needs to be done?" />
        {errors.title && <p className="error-text">{errors.title}</p>}
      </div>

      <div className="form-group">
        <label htmlFor="description">Description</label>
        <textarea id="description" name="description" value={form.description} onChange={handleChange}
          placeholder="Optional details…" />
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
        <div className="form-group">
          <label htmlFor="priority">Priority</label>
          <select id="priority" name="priority" value={form.priority} onChange={handleChange}>
            <option>High</option>
            <option>Medium</option>
            <option>Low</option>
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="status">Status</label>
          <select id="status" name="status" value={form.status} onChange={handleChange}>
            <option value="todo">To Do</option>
            <option value="in-progress">In Progress</option>
            <option value="done">Done</option>
          </select>
        </div>
      </div>

      <div className="form-group">
        <label htmlFor="dueDate">Due Date *</label>
        <input id="dueDate" name="dueDate" type="date" value={form.dueDate} onChange={handleChange}
          className={errors.dueDate ? 'input-error' : ''} />
        {errors.dueDate && <p className="error-text">{errors.dueDate}</p>}
      </div>

      <div className="form-actions">
        <button type="submit" className="btn">{submitLabel}</button>
      </div>
    </form>
  )
}

export default TaskForm
