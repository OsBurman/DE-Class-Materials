import React, { useState, useEffect } from 'react';
import axios from 'axios';

interface User {
  id: number;
  name: string;
  email: string;
}

interface UserListProps {
  useAxios?: boolean;
}

const API_URL = 'https://jsonplaceholder.typicode.com/users';

export function UserList({ useAxios = true }: UserListProps) {
  const [users, setUsers]       = useState<User[]>([]);
  const [loading, setLoading]   = useState(false);
  const [error, setError]       = useState<string | null>(null);
  // Incrementing refreshKey causes useEffect to re-run, triggering a new fetch.
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    let cancelled = false; // prevents setting state after the component unmounts

    setLoading(true);
    setError(null);

    if (useAxios) {
      axios
        .get<User[]>(API_URL)
        .then(response => {
          if (!cancelled) setUsers(response.data);
        })
        .catch(err => {
          if (!cancelled) setError(err.message ?? 'Unknown error');
        })
        .finally(() => {
          if (!cancelled) setLoading(false);
        });
    } else {
      fetch(API_URL)
        .then(res => {
          // fetch does NOT reject on HTTP errors — we must check res.ok manually.
          if (!res.ok) throw new Error(`HTTP ${res.status}`);
          return res.json() as Promise<User[]>;
        })
        .then(data => {
          if (!cancelled) setUsers(data);
        })
        .catch(err => {
          if (!cancelled) setError(err.message ?? 'Unknown error');
        })
        .finally(() => {
          if (!cancelled) setLoading(false);
        });
    }

    // Cleanup: mark the effect as stale so in-flight callbacks are ignored.
    return () => { cancelled = true; };
  }, [refreshKey, useAxios]); // re-run whenever refreshKey changes

  if (loading) return <p>Loading...</p>;
  if (error)   return <p className="error">Error: {error}</p>;

  return (
    <div>
      <h2>Users</h2>
      <ul>
        {users.map(user => (
          <li key={user.id}>
            {user.name} — {user.email}
          </li>
        ))}
      </ul>
      <button onClick={() => setRefreshKey(k => k + 1)}>Reload</button>
    </div>
  );
}

export default function App() {
  return <UserList useAxios={true} />;
}
