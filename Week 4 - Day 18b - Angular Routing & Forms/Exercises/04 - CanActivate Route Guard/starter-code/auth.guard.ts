// TODO 3 – Import CanActivateFn and inject from '@angular/core' / '@angular/router'
//          and also import Router from '@angular/router'
// TODO 3 – Import AuthService from './auth.service'

// TODO 4 – Export a const authGuard: CanActivateFn = (route, state) => { ... }
//   Inside the guard:
//   - const authService = inject(AuthService);
//   - const router      = inject(Router);
//   - if (authService.isLoggedIn) return true;
//   - else return router.createUrlTree(['/login']);
