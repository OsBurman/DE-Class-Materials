import { useContext } from 'react'
import { UserContext } from '../contexts/UserContext'

function Sidebar() {
  const { user } = useContext(UserContext)

  return (
    <aside className="sidebar">
      <h2>Navigation</h2>
      <ul>
        <li>🏠 Home</li>
        <li>📝 Articles</li>
        <li>🏷️ Tags</li>
        <li>ℹ️ About</li>
      </ul>
      {user?.role === 'admin' && (
        <div className="admin-panel">🔒 Admin Panel</div>
      )}
    </aside>
  )
}

export default Sidebar
