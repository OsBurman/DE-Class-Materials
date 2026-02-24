import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';

// login.component.ts — template-driven form
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
})
export class LoginComponent {
  // TODO Task 4: Declare email and password properties for ngModel binding
  email = '';
  password = '';
  errorMessage = '';

  constructor(private authService: AuthService) {}

  onSubmit(): void {
    // TODO Task 4: Call authService.login(email, password)
    // If successful, navigate to '/profile/' + authService.getUsername()
    // If not, set errorMessage = 'Invalid credentials'
  }
}
