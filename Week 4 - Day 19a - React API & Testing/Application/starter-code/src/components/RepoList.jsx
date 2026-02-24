// TODO Task 3: Build RepoList component
// Props: repos (array of GitHub repo objects)
// Sort repos by stargazers_count descending, show top 6
// Each repo card shows: name (link), description, stargazers_count, language

export default function RepoList({ repos }) {
  // TODO Task 3: Sort and slice repos
  // const topRepos = [...repos].sort((a, b) => b.stargazers_count - a.stargazers_count).slice(0, 6);

  if (repos.length === 0) {
    return <p className="empty">No repositories to display.</p>;
  }

  return (
    <div className="repo-list">
      <h2>Top Repositories</h2>
      {/* TODO Task 3: Map topRepos to repo cards */}
      <div className="repos-grid">
        <p>Map your repos here</p>
      </div>
    </div>
  );
}
