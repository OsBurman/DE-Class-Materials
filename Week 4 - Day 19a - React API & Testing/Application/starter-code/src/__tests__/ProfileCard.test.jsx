import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import ProfileCard from '../components/ProfileCard.jsx';

// Mock profile data
const mockProfile = {
  login: 'octocat',
  name: 'The Octocat',
  avatar_url: 'https://github.com/images/error/octocat_happy.gif',
  bio: 'A legendary GitHub mascot',
  public_repos: 8,
  followers: 12000,
  following: 9,
};

// TODO Task 6: Implement each test case
describe('ProfileCard', () => {
  it('renders profile name', () => {
    // TODO: render(<ProfileCard profile={mockProfile} />)
    //       expect(screen.getByText('The Octocat')).toBeInTheDocument();
    expect(true).toBe(true); // replace with real test
  });

  it('renders follower count', () => {
    // TODO: render and check for "12000" or "12,000" in the document
    expect(true).toBe(true);
  });

  it('renders placeholder when profile is null', () => {
    // TODO: render(<ProfileCard profile={null} />)
    //       expect(screen.getByText(/no profile/i)).toBeInTheDocument();
    expect(true).toBe(true);
  });

  it('renders avatar image with correct src', () => {
    // TODO: render and check img src attribute
    expect(true).toBe(true);
  });
});
