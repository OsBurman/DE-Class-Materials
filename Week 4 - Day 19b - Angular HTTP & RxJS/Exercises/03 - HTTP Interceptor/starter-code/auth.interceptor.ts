import { Injectable } from '@angular/core';
// TODO: import HttpInterceptor, HttpRequest, HttpHandler, HttpEvent from '@angular/common/http'
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor /* TODO: implements HttpInterceptor */ {
  // TODO: implement intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>>
  //   1. Clone req with setHeaders: { Authorization: 'Bearer my-secret-token' }
  //   2. Return next.handle(clonedReq)
}
