import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class XRequestTimeInterceptor implements HttpInterceptor {
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    if (
      req.url.indexOf('/token') === -1 &&
      req.url.indexOf('api.pwnedpasswords.com/range') === -1
    ) {
      return next.handle(
        req.clone({
          setHeaders: { 'X-Request-Time': new Date().toISOString() },
        })
      );
    } else {
      return next.handle(req);
    }
  }
}
