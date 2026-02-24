import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

// TODO Task 1: Implement ApiKeyInterceptor
// It implements HttpInterceptor
// In intercept():
//   1. Clone the request and add a header: 'x-app-name': 'angular-news-app'
//   2. Return next.handle(clonedRequest)
@Injectable()
export class ApiKeyInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // TODO: Clone the request and add the header
    // const cloned = request.clone({ setHeaders: { 'x-app-name': 'angular-news-app' } });
    // return next.handle(cloned);
    return next.handle(request);
  }
}
