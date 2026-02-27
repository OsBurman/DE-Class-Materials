import { useNavigate } from 'react-router-dom'
import { useTasks } from '../contexts/TaskContext'
import TaskForm from '../components/TaskForm'

function NewTaskPage() {
  const { dispatch } = useTasks()
  const navigate = useNavigate()

  function handleSubmit(formData) {
    dispatch({ type: 'ADD_TASK', payload: formData })
    navigate('/tasks')
  }

  return (
    <div>
      <h1 style={{ marginBottom: '1.5rem' }}>New Task</h1>
      <div className="form-card">
        <TaskForm onSubmit={handleSubmit} submitLabel="Create Task" />
      </div>
    </div>
  )
}

export default NewTaskPage
