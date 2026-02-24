import React from 'react';
import { render, screen } from '@testing-library/react';
import { PostList } from './PostList';

afterEach(() => {
  jest.restoreAllMocks();
});

describe('PostList', () => {
  it('shows loading state initially', () => {
    // A never-resolving promise keeps the component in the loading state indefinitely.
    global.fetch = jest.fn().mockReturnValue(new Promise(() => {}));

    render(<PostList />);

    // getByText is synchronous — it checks the DOM right now.
    expect(screen.getByText('Loading...')).toBeInTheDocument();
  });

  it('renders posts on successful fetch', async () => {
    const fakePosts = [
      { id: 1, title: 'First Post' },
      { id: 2, title: 'Second Post' },
    ];

    // mockResolvedValueOnce wraps the value in Promise.resolve automatically.
    global.fetch = jest.fn().mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(fakePosts),
    });

    render(<PostList />);

    // findByText is async — it polls until the element appears or times out.
    await screen.findByText('First Post');
    expect(screen.getByText('Second Post')).toBeInTheDocument();
  });

  it('shows error message when fetch fails', async () => {
    // mockRejectedValueOnce makes the promise reject with the given error.
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network Error'));

    render(<PostList />);

    // findByTestId waits for the element with data-testid="error" to appear.
    const errorEl = await screen.findByTestId('error');
    expect(errorEl).toHaveTextContent('Network Error');
  });
});
