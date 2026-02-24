import { renderHook, act } from '@testing-library/react';
import { useCounter } from './useCounter';

describe('useCounter', () => {
  it('initialises count to the provided value', () => {
    const { result } = renderHook(() => useCounter(5));
    // result.current holds the latest return value of the hook.
    expect(result.current.count).toBe(5);
  });

  it('increments count by 1', () => {
    const { result } = renderHook(() => useCounter());
    // act() flushes all state updates and effects synchronously.
    act(() => {
      result.current.increment();
    });
    expect(result.current.count).toBe(1);
  });

  it('decrements count by 1', () => {
    const { result } = renderHook(() => useCounter());
    act(() => {
      result.current.decrement();
    });
    expect(result.current.count).toBe(-1);
  });

  it('resets to the initial value', () => {
    const { result } = renderHook(() => useCounter());
    act(() => {
      result.current.increment();
      result.current.increment();
    });
    expect(result.current.count).toBe(2);

    act(() => {
      result.current.reset();
    });
    // After reset, count should be back to the initialValue (0).
    expect(result.current.count).toBe(0);
  });
});
