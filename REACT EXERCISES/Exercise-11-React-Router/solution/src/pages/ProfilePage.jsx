function ProfilePage({ user }) {
  return (
    <div className="page">
      <h1>My Profile</h1>
      <div className="profile-card">
        <div style={{ fontSize: '3rem', marginBottom: '0.5rem' }}>👤</div>
        <h2>{user.name}</h2>
        <p style={{ color: '#64748b' }}>Member since {new Date().getFullYear()}</p>
        <div style={{ marginTop: '1rem', padding: '0.75rem', background: '#f1f5f9', borderRadius: '8px', fontSize: '0.9rem' }}>
          <strong>Account Status:</strong> Active ✅
        </div>
      </div>
    </div>
  )
}

export default ProfilePage
