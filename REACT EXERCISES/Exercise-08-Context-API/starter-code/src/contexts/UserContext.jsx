import { createContext, useState, useContext } from 'react'

// TODO 1: Create UserContext using createContext() and export it.

// TODO 2: Build and export UserProvider({ children }):
//   - Add `user` state initialised to null.
//   - Return <UserContext.Provider value={{ user, setUser }}>{children}</UserContext.Provider>

// TODO 3 (Bonus): Export a custom hook `useUser()` that returns useContext(UserContext).

export const UserContext = createContext(null)

export function UserProvider({ children }) {
  // TODO: implement
  return <>{children}</>
}
