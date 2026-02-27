import { Routes, Route } from 'react-router-dom'
import './App.css'
import Layout from './components/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import DashboardPage from './pages/DashboardPage'
import TaskListPage from './pages/TaskListPage'
import TaskDetailPage from './pages/TaskDetailPage'
import NewTaskPage from './pages/NewTaskPage'
import EditTaskPage from './pages/EditTaskPage'
import LoginPage from './pages/LoginPage'
import ProfilePage from './pages/ProfilePage'
import NotFoundPage from './pages/NotFoundPage'

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<DashboardPage />} />
        <Route path="tasks" element={<TaskListPage />} />
        <Route path="tasks/new" element={<ProtectedRoute><NewTaskPage /></ProtectedRoute>} />
        <Route path="tasks/:id" element={<ProtectedRoute><TaskDetailPage /></ProtectedRoute>} />
        <Route path="tasks/:id/edit" element={<ProtectedRoute><EditTaskPage /></ProtectedRoute>} />
        <Route path="login" element={<LoginPage />} />
        <Route path="profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}

export default App
