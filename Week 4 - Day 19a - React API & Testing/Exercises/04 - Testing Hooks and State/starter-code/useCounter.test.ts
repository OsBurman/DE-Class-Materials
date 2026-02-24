import { renderHook, act } from '@testing-library/react';
import { useCounter } from './useCounter';

// TODO: write a describe('useCounter', () => { ... }) block with 4 tests:

// Test 1 – "initialises count to the provided value"
//   const { result } = renderHook(() => useCounter(5));
//   TODO: assert result.current.count === 5

// Test 2 – "increments count by 1"
//   const { result } = renderHook(() => useCounter());
//   TODO: wrap result.current.increment() in act(...)
//   TODO: assert result.current.count === 1

// Test 3 – "decrements count by 1"
//   const { result } = renderHook(() => useCounter());
//   TODO: wrap result.current.decrement() in act(...)
//   TODO: assert result.current.count === -1

// Test 4 – "resets to the initial value"
//   const { result } = renderHook(() => useCounter());
//   TODO: call increment() twice, then reset() — all inside act(...)
//   TODO: assert result.current.count === 0
