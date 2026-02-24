// TODO Task 5: Import useState and your hooks/components
import { useState } from 'react';
import ProfileCard from './components/ProfileCard.jsx';
import RepoList from './components/RepoList.jsx';
import ErrorBoundary from './components/ErrorBoundary.jsx';
import { useGitHubProfile } from './hooks/useGitHubProfile.js';

// Read default username from .env file
// Access with import.meta.env.VITE_DEFAULT_USERNAME
const DEFAULT_USERNAME = import.meta.env.VITE_DEFAULT_USERNAME || 'octocat';

export default function App() {
  const [inputValue, setInputValue] = useState(DEFAULT_USERNAME);
  const [username, setUsername] = useState(DEFAULT_USERNAME);

  // TODO Task 5: Use the useGitHubProfile hook
  // const { profile, repos, loading, error } = useGitHubProfile(username);

  function handleSearch(e) {
    e.preventDefault();
    // TODO Task 5: Update username to trigger a new fetch
  }

  return (
    <div className="app">
      <header>
        <h1>GitHub Profile Viewer</h1>
        <form onSubmit={handleSearch} className="search-form">
          <input
            value={inputValue}
            onChange={e => setInputValue(e.target.value)}
            placeholder="GitHub username..."
            className="search-input"
          />
          <button type="submit" className="search-btn">Search</button>
        </form>
      </header>

      <main>
        {/* TODO Task 5: Show loading spinner when loading is true */}
        {/* TODO Task 5: Show error message when error is set */}

        {/* TODO Task 5: Wrap profile section in ErrorBoundary */}
        <ErrorBoundary>
          {/* TODO Task 5: Render ProfileCard and RepoList with fetched data */}
          <p className="hint">Enter a GitHub username above and click Search</p>
        </ErrorBoundary>
      </main>
    </div>
  );
}
