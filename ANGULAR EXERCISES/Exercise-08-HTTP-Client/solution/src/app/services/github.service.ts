import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface GitHubUser {
  login: string; id: number; avatar_url: string; html_url: string;
  name: string | null; bio: string | null; public_repos: number;
  followers: number; following: number; location: string | null; company: string | null;
}

export interface GitHubSearchResult {
  total_count: number;
  items: GitHubUser[];
}

export interface GitHubRepo {
  id: number; name: string; html_url: string; description: string | null;
  stargazers_count: number; forks_count: number; language: string | null;
}

const BASE_URL = 'https://api.github.com';

@Injectable({ providedIn: 'root' })
export class GithubService {
  private http = inject(HttpClient);

  searchUsers(query: string): Observable<GitHubSearchResult> {
    return this.http.get<GitHubSearchResult>(`${BASE_URL}/search/users?q=${query}&per_page=12`);
  }

  getUser(username: string): Observable<GitHubUser> {
    return this.http.get<GitHubUser>(`${BASE_URL}/users/${username}`);
  }

  getUserRepos(username: string): Observable<GitHubRepo[]> {
    return this.http.get<GitHubRepo[]>(`${BASE_URL}/users/${username}/repos?sort=stars&per_page=6`);
  }
}
