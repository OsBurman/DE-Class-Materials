import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { PostList } from './PostList';

// Restore the original fetch after every test so mocks don't bleed between tests.
afterEach(() => {
  jest.restoreAllMocks();
});

describe('PostList', () => {
  // Test 1 – "shows loading state initially"
  it('shows loading state initially', () => {
    // TODO: mock global.fetch to return a promise that never resolves:
    //   global.fetch = jest.fn().mockReturnValue(new Promise(() => {}));
    // TODO: render(<PostList />)
    // TODO: assert screen.getByText('Loading...') is in the document
  });

  // Test 2 – "renders posts on successful fetch"
  it('renders posts on successful fetch', async () => {
    const fakePosts = [
      { id: 1, title: 'First Post' },
      { id: 2, title: 'Second Post' },
    ];

    // TODO: mock global.fetch to resolve with fakePosts:
    //   global.fetch = jest.fn().mockResolvedValueOnce({
    //     ok: true,
    //     json: () => Promise.resolve(fakePosts),
    //   });

    // TODO: render(<PostList />)

    // TODO: await screen.findByText('First Post')  (findBy* waits for async updates)
    // TODO: assert 'Second Post' is also in the document
  });

  // Test 3 – "shows error message when fetch fails"
  it('shows error message when fetch fails', async () => {
    // TODO: mock global.fetch to reject:
    //   global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network Error'));

    // TODO: render(<PostList />)

    // TODO: await screen.findByTestId('error')
    // TODO: assert the error element contains the text 'Network Error'
  });
});
