function RepoCard({ repo }) {
  return (
    <div className="repo-card">
      <div className="repo-name">
        <a href={repo.html_url} target="_blank" rel="noreferrer">{repo.name}</a>
      </div>
      <div className="repo-desc">{repo.description || 'No description provided.'}</div>
      <div className="repo-meta">
        {repo.language && (
          <span>
            <span className="lang-dot" />
            {repo.language}
          </span>
        )}
        <span>⭐ {repo.stargazers_count}</span>
        <span>🍴 {repo.forks_count}</span>
      </div>
    </div>
  )
}

export default RepoCard
