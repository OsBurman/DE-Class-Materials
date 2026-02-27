import { createContext, useContext, useReducer } from 'react'
import { taskReducer, INITIAL_STATE } from '../reducers/taskReducer'

const TaskContext = createContext(null)

export function TaskProvider({ children }) {
  const [state, dispatch] = useReducer(taskReducer, INITIAL_STATE)

  return (
    <TaskContext.Provider value={{ state, dispatch }}>
      {children}
    </TaskContext.Provider>
  )
}

export function useTasks() {
  const ctx = useContext(TaskContext)
  if (!ctx) throw new Error('useTasks must be used inside TaskProvider')
  return ctx
}
