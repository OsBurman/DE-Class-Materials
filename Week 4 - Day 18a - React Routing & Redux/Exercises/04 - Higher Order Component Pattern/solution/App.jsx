// src/App.jsx  (solution)
import React from 'react';
import UserCard from './components/UserCard';
import withLoadingSpinner from './hocs/withLoadingSpinner';
import withAdminOnly from './hocs/withAdminOnly';

// Create enhanced versions of UserCard by applying HOCs
// withLoadingSpinner adds loading-gate behavior
const UserCardWithLoading = withLoadingSpinner(UserCard);

// withAdminOnly adds access-control behavior
const AdminUserCard = withAdminOnly(UserCard);

function App() {
  return (
    <div style={{ padding: '1rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
      <h1>HOC Demo</h1>

      {/* isLoading=true → spinner shown; name/role are forwarded but not rendered yet */}
      <UserCardWithLoading isLoading={true} name="Alice" role="Developer" />

      {/* isLoading=false → HOC passes through; UserCard renders normally */}
      <UserCardWithLoading isLoading={false} name="Bob" role="Designer" />

      <hr />

      {/* isAdmin=false → access denied message */}
      <AdminUserCard isAdmin={false} name="Charlie" role="Manager" />

      {/* isAdmin=true → HOC passes through; UserCard renders normally */}
      <AdminUserCard isAdmin={true} name="Dana" role="Admin" />
    </div>
  );
}

export default App;
