import React, { useState, useEffect } from 'react';
// TODO: import axios from 'axios'  (run: npm install axios)

interface User {
  id: number;
  name: string;
  email: string;
}

interface UserListProps {
  useAxios?: boolean; // when true use axios; when false use fetch
}

const API_URL = 'https://jsonplaceholder.typicode.com/users';

export function UserList({ useAxios = true }: UserListProps) {
  // TODO: declare state for users (User[]), loading (boolean), error (string | null)

  // TODO: declare a refreshKey state (number) that will be incremented to re-trigger the fetch

  useEffect(() => {
    // TODO: set loading to true and error to null at the start of every fetch

    // TODO: declare a `cancelled` flag to prevent state updates after unmount

    if (useAxios) {
      // TODO: call axios.get<User[]>(API_URL)
      //   .then(response => { if (!cancelled) set users from response.data })
      //   .catch(err    => { if (!cancelled) set error from err.message })
      //   .finally(     => { if (!cancelled) set loading to false })
    } else {
      // TODO: call fetch(API_URL)
      //   .then(res => { if (!res.ok) throw new Error(`HTTP ${res.status}`); return res.json(); })
      //   .then(data => { if (!cancelled) set users })
      //   .catch(err => { if (!cancelled) set error })
      //   .finally(  => { if (!cancelled) set loading to false })
    }

    // TODO: return a cleanup function that sets `cancelled = true`

    // TODO: add refreshKey (and useAxios) to the dependency array
  }, []);

  // TODO: if loading, return <p>Loading...</p>
  // TODO: if error,   return <p className="error">Error: {error}</p>

  return (
    <div>
      <h2>Users</h2>
      {/* TODO: render a <ul> with one <li> per user showing name — email */}
      <button
        onClick={() => {/* TODO: increment refreshKey to trigger re-fetch */}}
      >
        Reload
      </button>
    </div>
  );
}

export default function App() {
  return <UserList useAxios={true} />;
}
