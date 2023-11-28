import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { v4 as uuidv4 } from 'uuid';

@Injectable()
export class XRequestIdInterceptor implements HttpInterceptor {
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    if (
      req.url.indexOf('/token') === -1 &&
      req.url.indexOf('api.pwnedpasswords.com/range') === -1
    ) {
      const requestId = uuidv4();
      return next.handle(
        req.clone({
          setHeaders: { 'X-Request-ID': requestId },
        })
      );
    } else {
      return next.handle(req);
    }
  }
}
