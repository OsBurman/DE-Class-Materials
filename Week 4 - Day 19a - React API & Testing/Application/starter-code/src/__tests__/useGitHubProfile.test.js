import { renderHook, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { useGitHubProfile } from '../hooks/useGitHubProfile.js';

// TODO Task 7: Mock fetch using vi.fn()
const mockProfile = { login: 'testuser', name: 'Test User', avatar_url: '', bio: '', public_repos: 5, followers: 10, following: 5 };
const mockRepos   = [{ id: 1, name: 'repo-1', description: 'Test repo', stargazers_count: 42, language: 'JavaScript' }];

describe('useGitHubProfile', () => {
  beforeEach(() => {
    // TODO Task 7: Set up global.fetch mock
    // global.fetch = vi.fn();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should have initial state of loading: false and profile: null', () => {
    // TODO: renderHook(() => useGitHubProfile(''))
    //       const { result } = ...
    //       expect(result.current.profile).toBeNull();
    //       expect(result.current.loading).toBe(false);
    expect(true).toBe(true);
  });

  it('should set profile data on successful fetch', async () => {
    // TODO: Mock fetch to return mockProfile and mockRepos
    // global.fetch = vi.fn().mockResolvedValue({ ok: true, json: () => Promise.resolve(mockProfile) });
    // renderHook and waitFor loading to be false, then assert profile
    expect(true).toBe(true);
  });

  it('should set error on fetch failure', async () => {
    // TODO: Mock fetch to reject
    // Assert that error is not null after the hook resolves
    expect(true).toBe(true);
  });
});
