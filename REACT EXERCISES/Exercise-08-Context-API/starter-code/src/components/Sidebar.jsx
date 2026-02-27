import { useContext } from 'react'
import { UserContext } from '../contexts/UserContext'

// TODO 1: Use useContext(UserContext) to get `user`.
// TODO 2: Only render the "Admin Panel" section when user?.role === 'admin'.

function Sidebar() {
  // TODO: consume UserContext

  return (
    <aside className="sidebar">
      <h2>Navigation</h2>
      <ul>
        <li>🏠 Home</li>
        <li>📝 Articles</li>
        <li>🏷️ Tags</li>
        <li>ℹ️ About</li>
      </ul>
      {/* TODO 3: Conditionally render this admin panel */}
      <div className="admin-panel">
        🔒 Admin Panel
      </div>
    </aside>
  )
}

export default Sidebar
