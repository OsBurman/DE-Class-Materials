import { HttpInterceptorFn } from '@angular/common/http';

// TODO: Log `[HTTP] METHOD URL` to console before passing the request along
export const loggingInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req);
};
