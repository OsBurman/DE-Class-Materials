import { useState, useEffect } from 'react';

/**
 * TODO Task 1: Implement useGitHubProfile(username)
 *
 * State to manage:
 *   profile  — the user object from GitHub API
 *   repos    — array of repository objects
 *   loading  — boolean
 *   error    — error message string or null
 *
 * API endpoints:
 *   Profile: https://api.github.com/users/{username}
 *   Repos:   https://api.github.com/users/{username}/repos?per_page=30
 *
 * Steps in useEffect:
 * 1. If username is empty, return early.
 * 2. Set loading: true, error: null
 * 3. Fetch both endpoints in parallel: Promise.all([fetch(profileUrl), fetch(reposUrl)])
 * 4. Check response.ok on both — if not ok throw new Error(response.statusText)
 * 5. Parse both responses with .json()
 * 6. Set profile and repos state
 * 7. Catch errors — set error to err.message
 * 8. Finally — set loading: false
 *
 * Dependency array: [username]
 */
export function useGitHubProfile(username) {
  const [profile, setProfile] = useState(null);
  const [repos, setRepos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // TODO: implement fetch logic here
  }, [username]);

  return { profile, repos, loading, error };
}
