import React, { useState, useEffect } from 'react';
import { config } from './config';

interface Todo {
  id: number;
  title: string;
}

/** Displays the API base URL from .env and fetches a sample todo title. */
export function WeatherWidget() {
  const [title, setTitle]     = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`${config.apiBaseUrl}/todos/1`)
      .then(res => res.json())
      .then((data: Todo) => setTitle(data.title))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className={config.featureDarkMode ? 'dark' : ''}>
      <p data-testid="api-url">Using: {config.apiBaseUrl}</p>
      {loading ? <p>Loading...</p> : <p>{title}</p>}
    </div>
  );
}

export default function App() {
  return <WeatherWidget />;
}
