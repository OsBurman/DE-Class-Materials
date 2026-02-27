import { useState, useEffect } from 'react'

// TODO: Implement useFetch(url)
//
// 1. Create state: data (null), loading (true), error (null).
//
// 2. Add a useEffect that runs when `url` changes:
//    a. Create an AbortController: const controller = new AbortController()
//    b. Set loading true, clear error and data.
//    c. fetch(url, { signal: controller.signal })
//       .then(res => { if (!res.ok) throw new Error('HTTP ' + res.status); return res.json() })
//       .then(json => { setData(json); setLoading(false) })
//       .catch(err => { if (err.name !== 'AbortError') { setError(err.message); setLoading(false) } })
//    d. Return cleanup: return () => controller.abort()
//
// 3. Return { data, loading, error }.

export function useFetch(url) {
  // TODO: implement
  return { data: null, loading: false, error: null }
}
