// TODO 10: Render a single repository card.
// Props: { repo }  (GitHub API repo object)
// Display:
//   - repo.name          → .repo-name  (link to repo.html_url, target="_blank")
//   - repo.description   → .repo-desc  (show 'No description' if null)
//   - repo.language      → in .repo-meta with a coloured dot  (skip if null)
//   - repo.stargazers_count → ⭐ count in .repo-meta
//   - repo.forks_count   → 🍴 count in .repo-meta

function RepoCard({ repo }) {
  return (
    <div className="repo-card">
      {/* repo name link, description, meta (language, stars, forks) */}
    </div>
  )
}

export default RepoCard
