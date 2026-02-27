import { useNavigate, Link } from 'react-router-dom'
import { useTasks } from '../contexts/TaskContext'
import TaskForm from '../components/TaskForm'

// TODO 9: Use useParams to get `id` from the URL.
// Find the task in state.tasks where task.id === Number(id).
// Pass the task as `initialData` to TaskForm.
// On submit, dispatch UPDATE_TASK with { id: Number(id), ...formData }, then navigate to /tasks/:id.

function EditTaskPage() {
  const { state, dispatch } = useTasks()
  const navigate = useNavigate()
  // TODO 9: Add useParams and find the task
  const task = null

  if (!task) {
    return <p>Task not found. <Link to="/tasks" style={{ color: 'var(--accent)' }}>Back to Tasks</Link></p>
  }

  function handleSubmit(formData) {
    // TODO 9: dispatch UPDATE_TASK, then navigate to the task detail page
  }

  return (
    <div>
      <h1 style={{ marginBottom: '1.5rem' }}>Edit Task</h1>
      <div className="form-card">
        <TaskForm initialData={task} onSubmit={handleSubmit} submitLabel="Save Changes" />
      </div>
    </div>
  )
}

export default EditTaskPage
