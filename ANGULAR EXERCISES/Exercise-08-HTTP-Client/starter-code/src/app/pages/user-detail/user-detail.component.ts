import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { Observable } from 'rxjs';
import { GithubService, GitHubUser, GitHubRepo } from '../../services/github.service';

@Component({
  selector: 'app-user-detail',
  standalone: true,
  imports: [RouterLink, AsyncPipe],
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.css'],
})
export class UserDetailComponent implements OnInit {

  private route = inject(ActivatedRoute);
  private githubService = inject(GithubService);

  // TODO 11: Declare user$ and repos$ as Observables
  user$!: Observable<GitHubUser>;
  repos$!: Observable<GitHubRepo[]>;

  ngOnInit(): void {
    // TODO 10: Read the 'username' route param
    // TODO 11: Assign user$ and repos$ using the service methods
    const username = this.route.snapshot.paramMap.get('username');
    if (username) {
      // this.user$ = ...
      // this.repos$ = ...
    }
  }
}
