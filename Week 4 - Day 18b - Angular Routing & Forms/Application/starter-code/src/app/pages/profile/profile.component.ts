import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
})
export class ProfileComponent implements OnInit {
  username = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // TODO Task 8: Read the :username route parameter from ActivatedRoute
    // this.route.paramMap.subscribe(params => { this.username = params.get('username') ?? ''; });
  }

  logout(): void {
    // TODO Task 8: Call authService.logout() then navigate to '/'
  }
}
