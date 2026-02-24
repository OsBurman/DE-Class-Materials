import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

// A functional guard — just a plain const, no class required.
// inject() works here because Angular calls this function within an injection context.
export const authGuard: CanActivateFn = (_route, _state) => {
  const authService = inject(AuthService);
  const router      = inject(Router);

  if (authService.isLoggedIn) {
    return true; // allow navigation
  }

  // createUrlTree returns a UrlTree; Angular treats this as a redirect rather than a hard block.
  return router.createUrlTree(['/login']);
};
