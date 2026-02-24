import React from 'react';

interface ButtonProps {
  onClick: () => void;
  children: React.ReactNode;
}

export default function Button({ onClick, children }: ButtonProps) {
  return (
    <button
      onClick={onClick}
      style={{ margin: '0 0.25rem', padding: '0.4rem 0.8rem', cursor: 'pointer' }}
    >
      {children}
    </button>
  );
}
