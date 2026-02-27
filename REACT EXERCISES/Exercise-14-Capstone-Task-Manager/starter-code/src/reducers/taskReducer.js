import { INITIAL_TASKS } from '../data/initialTasks'

// TODO 3: Implement each action case.
//
// ADD_TASK    — payload: { title, description, priority, dueDate, status }
//               Generate id with Date.now(), add to tasks array.
//
// UPDATE_TASK — payload: { id, title, description, priority, dueDate, status }
//               Replace the task with matching id.
//
// DELETE_TASK — payload: id (number)
//               Remove task with matching id.
//
// SET_FILTER  — payload: { status?, search? }
//               Merge payload into state.filter.

export const INITIAL_STATE = {
  tasks: INITIAL_TASKS,
  filter: { status: 'all', search: '' },
}

export function taskReducer(state, action) {
  switch (action.type) {
    case 'ADD_TASK':
      return state // Replace with implementation
    case 'UPDATE_TASK':
      return state // Replace with implementation
    case 'DELETE_TASK':
      return state // Replace with implementation
    case 'SET_FILTER':
      return state // Replace with implementation
    default:
      return state
  }
}
