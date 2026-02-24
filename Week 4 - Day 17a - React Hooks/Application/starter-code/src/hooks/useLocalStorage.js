import { useState, useEffect } from 'react';

/**
 * TODO Task 1: Implement useLocalStorage(key, initialValue)
 *
 * Steps:
 * 1. Initialize state from localStorage.getItem(key).
 *    - If a value exists, parse it with JSON.parse().
 *    - If not, use initialValue.
 *    Hint: useState(() => { ... }) accepts a function for lazy initialization.
 *
 * 2. Use useEffect to sync value → localStorage whenever value changes.
 *    - localStorage.setItem(key, JSON.stringify(value))
 *    - Dependency array: [key, value]
 *
 * 3. Return [value, setValue] — same shape as useState.
 */
export function useLocalStorage(key, initialValue) {
  // TODO: implement
  const [value, setValue] = useState(initialValue);
  return [value, setValue];
}
