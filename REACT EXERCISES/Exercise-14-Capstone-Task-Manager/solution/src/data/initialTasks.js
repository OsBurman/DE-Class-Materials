const today = new Date()
const fmt = offset => {
  const d = new Date(today)
  d.setDate(d.getDate() + offset)
  return d.toISOString().split('T')[0]
}

export const INITIAL_TASKS = [
  { id: 1, title: 'Set up project repository', description: 'Initialise Git, add .gitignore, push to remote.', priority: 'High', status: 'done', dueDate: fmt(-10) },
  { id: 2, title: 'Design database schema', description: 'ERD for users, tasks, and tags tables.', priority: 'High', status: 'done', dueDate: fmt(-7) },
  { id: 3, title: 'Build REST API endpoints', description: 'CRUD routes for /tasks; add JWT middleware.', priority: 'High', status: 'in-progress', dueDate: fmt(-2) },
  { id: 4, title: 'Write unit tests for API', description: 'Cover all happy paths and error cases with Jest.', priority: 'Medium', status: 'in-progress', dueDate: fmt(3) },
  { id: 5, title: 'Implement front-end authentication', description: 'Login / logout flow with token storage.', priority: 'High', status: 'todo', dueDate: fmt(5) },
  { id: 6, title: 'Create dashboard UI', description: 'Stats cards, recent tasks widget, quick-add form.', priority: 'Medium', status: 'todo', dueDate: fmt(8) },
  { id: 7, title: 'Add drag-and-drop for Kanban', description: 'Allow tasks to be dragged between status columns.', priority: 'Low', status: 'todo', dueDate: fmt(14) },
  { id: 8, title: 'Deploy to production', description: 'CI/CD pipeline via GitHub Actions; deploy to Render.', priority: 'Medium', status: 'todo', dueDate: fmt(21) },
]
