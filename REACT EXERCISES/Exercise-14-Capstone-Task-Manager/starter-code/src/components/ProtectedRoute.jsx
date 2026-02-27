import { Navigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

// ProtectedRoute: renders children if user is logged in, otherwise redirects to /login.
// The component is already wired up — students don't need to modify this file.
function ProtectedRoute({ children }) {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  return children
}

export default ProtectedRoute
