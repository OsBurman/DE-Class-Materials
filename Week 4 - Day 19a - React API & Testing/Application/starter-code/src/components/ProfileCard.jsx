// TODO Task 2: Build ProfileCard component
// Props: profile (the GitHub user object, may be null)
// Display: avatar_url, name, bio, public_repos, followers, following
// Show a placeholder card when profile is null

export default function ProfileCard({ profile }) {
  if (!profile) {
    // TODO Task 2: Return a placeholder/skeleton card
    return <div className="profile-card placeholder"><p>No profile loaded</p></div>;
  }

  return (
    <div className="profile-card">
      {/* TODO Task 2: Display avatar using <img src={profile.avatar_url} alt={profile.login} /> */}
      {/* TODO Task 2: Display profile.name (or profile.login as fallback) */}
      {/* TODO Task 2: Display profile.bio if it exists (*ngIf equivalent: && short-circuit) */}
      {/* TODO Task 2: Display followers, following, public_repos stats */}
      <p className="placeholder-text">Build profile card here</p>
    </div>
  );
}
