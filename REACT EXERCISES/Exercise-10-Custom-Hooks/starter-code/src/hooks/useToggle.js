import { useState, useCallback } from 'react'

// TODO: Implement useToggle(initialValue = false)
//
// 1. Create state: value, initialised to initialValue.
//
// 2. Create toggle using useCallback:
//    const toggle = useCallback(() => setValue(prev => !prev), [])
//    Using useCallback ensures the toggle function reference stays stable.
//
// 3. Return [value, toggle].

export function useToggle(initialValue = false) {
  // TODO: implement
  const [value, setValue] = useState(initialValue)
  return [value, () => setValue(v => !v)]
}
