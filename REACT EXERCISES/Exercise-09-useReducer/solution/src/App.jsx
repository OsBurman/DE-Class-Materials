import KanbanBoard from './components/KanbanBoard'
import './App.css'

function App() {
  return (
    <div className="app">
      <header className="app-header">
        <h1>📋 Kanban Board</h1>
        <p>Manage your tasks with useReducer</p>
      </header>
      <KanbanBoard />
    </div>
  )
}

export default App
