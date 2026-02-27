function UserProfile({ user }) {
  return (
    <div className="user-profile">
      <img
        src={user.avatar_url}
        alt={`${user.login}'s avatar`}
        className="user-avatar"
      />
      <div className="user-info">
        <h2>{user.name || user.login}</h2>
        <div className="user-login">@{user.login}</div>
        {user.bio && <p className="user-bio">{user.bio}</p>}
        <div className="user-stats">
          <span><strong>{user.followers}</strong> followers</span>
          <span><strong>{user.following}</strong> following</span>
          <span><strong>{user.public_repos}</strong> repos</span>
        </div>
      </div>
    </div>
  )
}

export default UserProfile
