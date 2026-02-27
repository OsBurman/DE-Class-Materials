const STATUSES = ['todo', 'in-progress', 'done']

export const INITIAL_STATE = {
  tasks: [
    { id: 1, title: 'Set up project structure', status: 'done', priority: 'High' },
    { id: 2, title: 'Design database schema', status: 'done', priority: 'High' },
    { id: 3, title: 'Build REST API endpoints', status: 'in-progress', priority: 'High' },
    { id: 4, title: 'Write unit tests', status: 'in-progress', priority: 'Medium' },
    { id: 5, title: 'Create UI mockups', status: 'todo', priority: 'Medium' },
    { id: 6, title: 'Update documentation', status: 'todo', priority: 'Low' },
  ],
  filter: 'all',
}

export function tasksReducer(state, action) {
  switch (action.type) {
    case 'ADD_TASK': {
      const newTask = { id: Date.now(), title: action.payload.title, status: 'todo', priority: action.payload.priority }
      return { ...state, tasks: [...state.tasks, newTask] }
    }
    case 'DELETE_TASK':
      return { ...state, tasks: state.tasks.filter(t => t.id !== action.payload.id) }
    case 'MOVE_TASK': {
      return {
        ...state,
        tasks: state.tasks.map(t => {
          if (t.id !== action.payload.id) return t
          const idx = STATUSES.indexOf(t.status)
          const newIdx = Math.max(0, Math.min(STATUSES.length - 1, idx + action.payload.direction))
          return { ...t, status: STATUSES[newIdx] }
        }),
      }
    }
    case 'EDIT_TASK':
      return { ...state, tasks: state.tasks.map(t => t.id === action.payload.id ? { ...t, title: action.payload.title } : t) }
    case 'SET_FILTER':
      return { ...state, filter: action.payload.filter }
    default:
      return state
  }
}
