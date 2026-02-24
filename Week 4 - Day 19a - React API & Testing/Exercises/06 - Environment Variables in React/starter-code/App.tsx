import React, { useState, useEffect } from 'react';
import { config } from './config';

interface Todo {
  id: number;
  title: string;
}

export function WeatherWidget() {
  const [title, setTitle]   = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // TODO: fetch from `${config.apiBaseUrl}/todos/1`
    //   .then(res => res.json())
    //   .then((data: Todo) => setTitle(data.title))
    //   .finally(() => setLoading(false))
  }, []);

  return (
    // TODO: add the 'dark' CSS class when config.featureDarkMode is true
    <div>
      {/* TODO: render <p data-testid="api-url">Using: {config.apiBaseUrl}</p> */}
      {loading ? <p>Loading...</p> : <p>{title}</p>}
    </div>
  );
}

export default function App() {
  return <WeatherWidget />;
}
