// src/App.jsx  (starter)
import React from 'react';
import UserCard from './components/UserCard';
import withLoadingSpinner from './hocs/withLoadingSpinner';
import withAdminOnly from './hocs/withAdminOnly';

// TODO 4: Wrap UserCard with withLoadingSpinner to create UserCardWithLoading.
const UserCardWithLoading = UserCard; // replace with HOC wrapping

// TODO 5: Wrap UserCard with withAdminOnly to create AdminUserCard.
const AdminUserCard = UserCard; // replace with HOC wrapping

function App() {
  return (
    <div style={{ padding: '1rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
      <h1>HOC Demo</h1>

      {/* TODO 6: Render UserCardWithLoading with isLoading=true, name="Alice", role="Developer" */}
      {/* TODO 7: Render UserCardWithLoading with isLoading=false, name="Bob", role="Designer" */}

      <hr />

      {/* TODO 8: Render AdminUserCard with isAdmin=false, name="Charlie", role="Manager" */}
      {/* TODO 9: Render AdminUserCard with isAdmin=true, name="Dana", role="Admin" */}
    </div>
  );
}

export default App;
