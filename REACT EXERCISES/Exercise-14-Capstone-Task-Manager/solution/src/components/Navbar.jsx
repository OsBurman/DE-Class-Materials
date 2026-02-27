import { NavLink, Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { useTheme } from '../contexts/ThemeContext'
import { useTasks } from '../contexts/TaskContext'

function Navbar() {
  const { user, logout } = useAuth()
  const { theme, toggleTheme } = useTheme()
  const { state } = useTasks()

  const todoCount = state.tasks.filter(t => t.status === 'todo').length

  return (
    <nav>
      <Link to="/" className="nav-brand">📋 TaskManager</Link>
      <NavLink to="/" end>Dashboard</NavLink>
      <NavLink to="/tasks">
        Tasks {todoCount > 0 && <span className="nav-badge">{todoCount}</span>}
      </NavLink>
      {user && <NavLink to="/profile" className="nav-user">👤 {user.name}</NavLink>}
      <button className="btn-theme" onClick={toggleTheme} title="Toggle theme">
        {theme === 'light' ? '🌙' : '☀️'}
      </button>
      {user
        ? <button className="btn-nav" onClick={logout}>Logout</button>
        : <NavLink to="/login">Login</NavLink>
      }
    </nav>
  )
}

export default Navbar
