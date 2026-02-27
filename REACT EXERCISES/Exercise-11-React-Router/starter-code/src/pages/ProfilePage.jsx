function ProfilePage({ user }) {
  return (
    <div className="page">
      <h1 style={{ marginBottom: '1.5rem' }}>Your Profile</h1>
      <div className="profile-card">
        <p style={{ fontSize: '3rem', marginBottom: '0.5rem' }}>👤</p>
        <h2>{user?.name}</h2>
        <p style={{ color: '#64748b', marginTop: '0.5rem' }}>Member since today</p>
      </div>
    </div>
  )
}

export default ProfilePage
