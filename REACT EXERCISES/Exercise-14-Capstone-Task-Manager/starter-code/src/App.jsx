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

// TODO 4: Set up all routes below.
// Layout should be the parent route element (it renders the Navbar + <Outlet />).
// Protected routes (new task, task detail, edit task, profile) should be wrapped in <ProtectedRoute>.
// Route map:
//   /                   → DashboardPage
//   /tasks              → TaskListPage
//   /tasks/new          → NewTaskPage (protected)
//   /tasks/:id          → TaskDetailPage (protected)
//   /tasks/:id/edit     → EditTaskPage (protected)
//   /login              → LoginPage
//   /profile            → ProfilePage (protected)
//   *                   → NotFoundPage

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        {/* Add routes here */}
      </Route>
    </Routes>
  )
}

export default App
