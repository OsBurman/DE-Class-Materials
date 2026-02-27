import { useParams, useNavigate, Link } from 'react-router-dom'
import { useTasks } from '../contexts/TaskContext'
import TaskForm from '../components/TaskForm'

function EditTaskPage() {
  const { id } = useParams()
  const { state, dispatch } = useTasks()
  const navigate = useNavigate()
  const task = state.tasks.find(t => t.id === Number(id))

  if (!task) {
    return <p>Task not found. <Link to="/tasks" style={{ color: 'var(--accent)' }}>Back to Tasks</Link></p>
  }

  function handleSubmit(formData) {
    dispatch({ type: 'UPDATE_TASK', payload: { id: Number(id), ...formData } })
    navigate(`/tasks/${id}`)
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
