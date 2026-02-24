// =============================================================================
// Day 19b — Angular HTTP & RxJS  |  Part 1
// File: 02-http-interceptors.ts
//
// Topics covered:
//   1. What interceptors are and why they exist
//   2. Class-based HttpInterceptor (Angular 14 and earlier style)
//   3. Functional interceptors — HttpInterceptorFn (Angular 15+)
//   4. Registering interceptors (NgModule + standalone)
//   5. Auth token attachment on every request
//   6. Response interceptor — global 401 redirect
//   7. Error handling interceptor with catchError
//   8. Loading spinner interceptor (request counting)
//   9. Interceptor chain order
// =============================================================================

import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
  HttpErrorResponse,
  HTTP_INTERCEPTORS,
} from '@angular/common/http';
import {
  HttpInterceptorFn,
  HttpHandlerFn,
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, switchMap, take, tap, finalize } from 'rxjs/operators';
import { Router } from '@angular/router';

// ---------------------------------------------------------------------------
// Section 1 — What is an HTTP Interceptor?
// ---------------------------------------------------------------------------
//
// Interceptors sit in the pipeline between your service and the actual HTTP call.
// They can inspect, transform, or retry EVERY request and response transparently.
//
//  Component → Service → [Interceptor A] → [Interceptor B] → Backend
//                     ←                ←                 ←
//
// Common uses:
//   ✅ Attach JWT tokens to every outgoing request
//   ✅ Log requests and timing
//   ✅ Show/hide a global loading spinner
//   ✅ Handle 401 (token refresh) or 403 globally
//   ✅ Add correlation IDs for distributed tracing
//   ⚠️  Interceptors see ALL HttpClient calls — including those made by 3rd-party
//       Angular libraries. Guard with URL checks if needed.

// ---------------------------------------------------------------------------
// Section 2 — Class-Based Interceptor (Angular ≤14 / NgModule style)
// ---------------------------------------------------------------------------

@Injectable()
export class AuthTokenInterceptor implements HttpInterceptor {
  //
  // intercept() receives:
  //   req  — the outgoing HttpRequest (IMMUTABLE)
  //   next — the next handler in the chain
  //
  // To modify the request you MUST clone it (req.clone({...})).
  // Never mutate req directly — Angular enforces immutability.

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    // Read the stored JWT from localStorage (or an AuthService in real code)
    const token = localStorage.getItem('access_token');

    if (token) {
      // Clone the request and add the Authorization header
      const authenticatedReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`),
      });

      // Pass the cloned (modified) request down the chain
      return next.handle(authenticatedReq);
    }

    // No token — pass the original request unchanged
    return next.handle(req);
  }
}

// ---------------------------------------------------------------------------
// Section 3 — Logging Interceptor (class-based)
// ---------------------------------------------------------------------------

@Injectable()
export class LoggingInterceptor implements HttpInterceptor {
  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const startTime = Date.now();

    console.log(`→ [HTTP] ${req.method} ${req.url}`);

    return next.handle(req).pipe(
      // tap lets us peek at each emitted value WITHOUT modifying it
      tap({
        next: (event) => {
          if (event instanceof HttpResponse) {
            const elapsed = Date.now() - startTime;
            console.log(
              `← [HTTP] ${event.status} ${req.url} (${elapsed}ms)`
            );
          }
        },
        error: (err: HttpErrorResponse) => {
          console.error(`← [HTTP ERROR] ${err.status} ${req.url}`, err.message);
        },
      })
    );
  }
}

// ---------------------------------------------------------------------------
// Section 4 — Error Handling Interceptor (global 401 redirect, class-based)
// ---------------------------------------------------------------------------

@Injectable()
export class GlobalErrorInterceptor implements HttpInterceptor {
  constructor(private router: Router) {}

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token expired or missing — redirect to login
          console.warn('401 Unauthorized — redirecting to /login');
          this.router.navigate(['/login']);
          return throwError(() => new Error('Session expired. Please log in again.'));
        }

        if (error.status === 403) {
          console.warn('403 Forbidden — insufficient permissions');
          this.router.navigate(['/forbidden']);
          return throwError(() => new Error('Access denied.'));
        }

        if (error.status === 0) {
          return throwError(
            () => new Error('Network error — check your internet connection.')
          );
        }

        // For all other errors, pass them through as-is
        return throwError(() => error);
      })
    );
  }
}

// ---------------------------------------------------------------------------
// Section 5 — Functional Interceptor (Angular 15+ preferred style)
// ---------------------------------------------------------------------------
//
// Starting with Angular 15, you can write interceptors as plain functions.
// No class, no @Injectable, no implements — just a function matching HttpInterceptorFn.
//
// This is the RECOMMENDED approach for modern standalone Angular apps.

export const authTokenInterceptorFn: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  const token = localStorage.getItem('access_token');

  if (!token) {
    return next(req); // No change
  }

  const authenticatedReq = req.clone({
    headers: req.headers.set('Authorization', `Bearer ${token}`),
  });

  return next(authenticatedReq);
};

// ---------------------------------------------------------------------------
// Section 6 — Loading Spinner Interceptor (functional style)
// ---------------------------------------------------------------------------
//
// A common pattern: count active requests and show/hide a spinner.

// In a real app this would be an injectable SpinnerService
let activeRequestCount = 0;

export const loadingSpinnerInterceptorFn: HttpInterceptorFn = (req, next) => {
  activeRequestCount++;
  showSpinner(); // e.g. spinnerService.show()

  return next(req).pipe(
    finalize(() => {
      activeRequestCount--;
      if (activeRequestCount === 0) {
        hideSpinner(); // e.g. spinnerService.hide()
      }
    })
  );
};

function showSpinner(): void {
  console.log('🔄 Spinner shown (active requests:', activeRequestCount, ')');
}

function hideSpinner(): void {
  console.log('✅ Spinner hidden');
}

// ---------------------------------------------------------------------------
// Section 7 — Token Refresh Interceptor (advanced pattern)
// ---------------------------------------------------------------------------
//
// When a 401 is received, try to refresh the access token silently,
// then replay the original failed request.
// This pattern uses BehaviorSubject as a lock to queue concurrent requests.

@Injectable()
export class TokenRefreshInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    // Skip the refresh endpoint itself to prevent infinite loops
    if (req.url.includes('/auth/refresh')) {
      return next.handle(req);
    }

    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status !== 401) {
          return throwError(() => error);
        }

        if (this.isRefreshing) {
          // Another request is already refreshing — queue this one
          return this.refreshTokenSubject.pipe(
            filter((token): token is string => token !== null),
            take(1),
            switchMap((newToken) => next.handle(this.addToken(req, newToken)))
          );
        }

        // Start the refresh flow
        this.isRefreshing = true;
        this.refreshTokenSubject.next(null); // Signal: refreshing in progress

        // In a real app: call this.authService.refreshToken()
        const refreshToken = localStorage.getItem('refresh_token') ?? '';
        const mockRefresh$: Observable<{ access_token: string }> =
          // Simulated — in real code: return this.http.post('/auth/refresh', { refreshToken })
          new Observable((obs) => {
            obs.next({ access_token: 'new-access-token-xyz' });
            obs.complete();
          });

        return mockRefresh$.pipe(
          switchMap(({ access_token }) => {
            this.isRefreshing = false;
            localStorage.setItem('access_token', access_token);
            this.refreshTokenSubject.next(access_token); // Unblock queued requests
            return next.handle(this.addToken(req, access_token));
          }),
          catchError((refreshError) => {
            this.isRefreshing = false;
            localStorage.removeItem('access_token');
            // router.navigate(['/login']) in a real app
            return throwError(() => refreshError);
          })
        );
      })
    );
  }

  private addToken(
    req: HttpRequest<unknown>,
    token: string
  ): HttpRequest<unknown> {
    return req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`),
    });
  }
}

// ---------------------------------------------------------------------------
// Section 8 — Registering Interceptors
// ---------------------------------------------------------------------------
//
// ---- NgModule-based apps (Angular ≤17 without standalone) ----
//
//  @NgModule({
//    providers: [
//      { provide: HTTP_INTERCEPTORS, useClass: AuthTokenInterceptor,  multi: true },
//      { provide: HTTP_INTERCEPTORS, useClass: LoggingInterceptor,    multi: true },
//      { provide: HTTP_INTERCEPTORS, useClass: GlobalErrorInterceptor, multi: true },
//    ]
//  })
//  export class AppModule {}
//
// ⚠️  The ORDER matters!  Interceptors run in registration order on the way OUT
//     and in REVERSE order on the way IN (response path).
//
//
// ---- Standalone / Angular 15+ (functional interceptors) ----
//
//  bootstrapApplication(AppComponent, {
//    providers: [
//      provideHttpClient(
//        withInterceptors([
//          authTokenInterceptorFn,
//          loadingSpinnerInterceptorFn,
//        ])
//      )
//    ]
//  });
//
//
// ---- Mixing class + functional (when migrating) ----
//
//  provideHttpClient(
//    withInterceptors([authTokenInterceptorFn]),
//    withInterceptorsFromDi()   // picks up HTTP_INTERCEPTORS token providers
//  )

// ---------------------------------------------------------------------------
// Section 9 — Interceptor Chain Order Diagram
// ---------------------------------------------------------------------------
//
// Registered order:  [AuthToken] → [Logging] → [GlobalError]
//
// Outgoing request (order 1 → 2 → 3 → Backend):
//   Component → AuthTokenInterceptor → LoggingInterceptor → GlobalErrorInterceptor → Backend
//
// Incoming response (reverse: 3 → 2 → 1 → Component):
//   Backend → GlobalErrorInterceptor → LoggingInterceptor → AuthTokenInterceptor → Component
//
// ✅ Best practice:
//    - Put auth token attachment FIRST (most requests need the token)
//    - Put error handling LAST in registration (first in response processing)
//    - Keep each interceptor focused on ONE concern (single responsibility)
