import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { GithubService, GitHubUser } from '../../services/github.service';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
})
export class SearchComponent {

  // TODO 6: Inject GithubService and Router
  private githubService = inject(GithubService);
  private router = inject(Router);

  // TODO 7: Declare these properties:
  query = '';
  users: GitHubUser[] = [];
  loading = false;
  error = '';
  totalCount = 0;

  // TODO 8: Implement search():
  //   1. If query is empty, return early
  //   2. Set loading = true, error = ''
  //   3. Call githubService.searchUsers(query).subscribe({
  //        next: (result) => { this.users = result.items; this.totalCount = result.total_count; this.loading = false; }
  //        error: (err) => { this.error = 'Failed to fetch users.'; this.loading = false; }
  //      })
  search(): void {
    // your code here
  }

  // TODO 9: Implement goToUser(login: string) — navigate to /users/:login
  goToUser(login: string): void {
    // your code here
  }
}
