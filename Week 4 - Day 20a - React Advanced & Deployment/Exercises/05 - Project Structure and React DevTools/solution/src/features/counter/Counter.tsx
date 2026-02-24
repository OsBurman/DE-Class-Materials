import React, { memo } from 'react';
import { Button } from '../../components/ui';
import { useCounter } from './useCounter';

export default function Counter() {
  const { count, increment, decrement, reset } = useCounter();
  return (
    <div>
      <p>Counter: {count}</p>
      <Button onClick={increment}>Increment</Button>
      <Button onClick={decrement}>Decrement</Button>
      <Button onClick={reset}>Reset</Button>
    </div>
  );
}

// Part B — StaticLabel:
// React.memo prevents re-renders when the parent (App) re-renders due to Counter's state change.
// Without memo, StaticLabel would re-render every time App's children tree re-evaluates.
export const StaticLabel = memo(function StaticLabel() {
  console.log('StaticLabel rendered');
  return <p>This never changes</p>;
});
