// src/components/UserCard.jsx  (solution)
import React from 'react';

// Simple presentational component — HOCs enhance it without touching this file
function UserCard({ name, role }) {
  return (
    <div style={{
      border: '1px solid #ccc',
      borderRadius: '6px',
      padding: '0.75rem 1rem',
      maxWidth: '240px',
      background: '#fff',
    }}>
      <strong style={{ fontSize: '1.1rem' }}>{name}</strong>
      <p style={{ margin: '0.25rem 0 0' }}>Role: {role}</p>
    </div>
  );
}

export default UserCard;
