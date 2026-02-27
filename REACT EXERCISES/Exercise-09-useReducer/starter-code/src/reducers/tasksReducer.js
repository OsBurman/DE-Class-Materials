// TODO 1: Define and export INITIAL_STATE with:
//   tasks: array of 6 tasks (see below), filter: 'all'
//
//   Sample tasks:
//   { id: 1, title: 'Set up project structure', status: 'done', priority: 'High' },
//   { id: 2, title: 'Design database schema', status: 'done', priority: 'High' },
//   { id: 3, title: 'Build REST API endpoints', status: 'in-progress', priority: 'High' },
//   { id: 4, title: 'Write unit tests', status: 'in-progress', priority: 'Medium' },
//   { id: 5, title: 'Create UI mockups', status: 'todo', priority: 'Medium' },
//   { id: 6, title: 'Update documentation', status: 'todo', priority: 'Low' },

// TODO 2: Define and export tasksReducer(state, action).
//   Handle these action types:
//
//   'ADD_TASK':
//     const newTask = { id: Date.now(), title: action.payload.title,
//                       status: 'todo', priority: action.payload.priority }
//     return { ...state, tasks: [...state.tasks, newTask] }
//
//   'DELETE_TASK':
//     return { ...state, tasks: state.tasks.filter(t => t.id !== action.payload.id) }
//
//   'MOVE_TASK':
//     Define STATUSES = ['todo', 'in-progress', 'done']
//     Find the current status index, add action.payload.direction (+1 or -1), clamp to [0, 2].
//     Return new state with that task's status updated.
//
//   'EDIT_TASK':
//     Map over tasks, return the matching task with title updated.
//
//   'SET_FILTER':
//     return { ...state, filter: action.payload.filter }
//
//   default: return state

export const INITIAL_STATE = {
  tasks: [],
  filter: 'all',
}

export function tasksReducer(state, action) {
  // TODO: implement all cases
  return state
}
