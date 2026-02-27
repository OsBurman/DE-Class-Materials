// TODO 9: Render the user's profile.
// Props: { user }  (GitHub API user object)
// Display:
//   - user.avatar_url  → <img className="user-avatar" />
//   - user.name        → heading (fall back to user.login if null)
//   - user.login       → @username in .user-login
//   - user.bio         → .user-bio (skip if null)
//   - user.followers, user.following, user.public_repos → .user-stats

function UserProfile({ user }) {
  return (
    <div className="user-profile">
      {/* Avatar, name, login, bio, stats */}
    </div>
  )
}

export default UserProfile
