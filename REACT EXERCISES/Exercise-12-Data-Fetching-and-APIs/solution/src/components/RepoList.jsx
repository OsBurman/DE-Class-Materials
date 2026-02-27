import RepoCard from './RepoCard'

function RepoList({ repos }) {
  if (repos.length === 0) {
    return <p style={{ color: '#94a3b8' }}>No public repositories found.</p>
  }

  return (
    <div className="repo-list">
      <h2>📁 Recent Repositories ({repos.length})</h2>
      <div className="repo-grid">
        {repos.map(repo => (
          <RepoCard key={repo.id} repo={repo} />
        ))}
      </div>
    </div>
  )
}

export default RepoList
