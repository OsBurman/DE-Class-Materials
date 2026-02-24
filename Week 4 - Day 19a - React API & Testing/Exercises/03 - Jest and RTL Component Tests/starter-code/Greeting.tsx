import React, { useState } from 'react';

interface GreetingProps {
  name: string;
}

export function Greeting({ name: initialName }: GreetingProps) {
  const [name, setName] = useState(initialName);

  return (
    <div>
      <h1>Hello, {name}!</h1>
      <button onClick={() => setName('World')}>Change Name</button>
    </div>
  );
}
