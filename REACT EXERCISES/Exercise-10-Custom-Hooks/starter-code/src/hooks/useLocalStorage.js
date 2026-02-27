import { useState, useEffect } from 'react'

// TODO: Implement useLocalStorage(key, initialValue)
//
// 1. Initialise state lazily:
//    useState(() => {
//      try {
//        const stored = localStorage.getItem(key)
//        return stored ? JSON.parse(stored) : initialValue
//      } catch { return initialValue }
//    })
//
// 2. Add a useEffect that runs when `value` changes:
//    localStorage.setItem(key, JSON.stringify(value))
//
// 3. Return [value, setValue] — same shape as useState.

export function useLocalStorage(key, initialValue) {
  // TODO: implement
  const [value, setValue] = useState(initialValue)
  return [value, setValue]
}
