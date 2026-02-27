import { INITIAL_TASKS } from '../data/initialTasks'

export const INITIAL_STATE = {
  tasks: INITIAL_TASKS,
  filter: { status: 'all', search: '' },
}

export function taskReducer(state, action) {
  switch (action.type) {
    case 'ADD_TASK':
      return {
        ...state,
        tasks: [...state.tasks, { id: Date.now(), ...action.payload }],
      }
    case 'UPDATE_TASK':
      return {
        ...state,
        tasks: state.tasks.map(t =>
          t.id === action.payload.id ? { ...t, ...action.payload } : t
        ),
      }
    case 'DELETE_TASK':
      return {
        ...state,
        tasks: state.tasks.filter(t => t.id !== action.payload),
      }
    case 'SET_FILTER':
      return {
        ...state,
        filter: { ...state.filter, ...action.payload },
      }
    default:
      return state
  }
}
