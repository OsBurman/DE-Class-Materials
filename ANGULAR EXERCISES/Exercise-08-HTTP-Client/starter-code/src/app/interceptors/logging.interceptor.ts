// TODO 13: Implement a functional HTTP interceptor.
// It should console.log the request method and URL, then pass the request along with next(req).

import { HttpInterceptorFn } from '@angular/common/http';

export const loggingInterceptor: HttpInterceptorFn = (req, next) => {
  // TODO: Log the request
  // console.log(`[HTTP] ${req.method} ${req.url}`);

  // Pass the request to the next handler
  return next(req);
};
