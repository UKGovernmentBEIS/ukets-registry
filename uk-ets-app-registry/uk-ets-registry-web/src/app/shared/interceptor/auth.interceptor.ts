import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, mergeMap } from 'rxjs/operators';
import { AuthApiService } from 'src/app/auth/auth-api.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(protected authApiService: AuthApiService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        if (err.status === 403 || err.status === 401) {
          if (err.url.indexOf('/token') === -1) {
            // Here is the auth logic
            const wwwAuthenticateHeader = err.headers.get('WWW-authenticate');
            // When using uma, a WWW-authenticate header should be returned by the resource server
            if (!wwwAuthenticateHeader) {
              throwError(
                () =>
                  new Error(
                    'When using uma, a WWW-authenticate header should be returned by the resource server'
                  )
              );
            }
            // When using uma, a WWW-authenticate header should contain uma data
            if (wwwAuthenticateHeader.indexOf('UMA') === -1) {
              throwError(
                () =>
                  new Error(
                    'When using uma, a WWW-authenticate header should contain uma data'
                  )
              );
            }
            const params = wwwAuthenticateHeader.split(',');
            let ticket: string;
            // try to extract the permission ticket from the WWW-Authenticate header
            for (const nameValuePair of params) {
              const param = nameValuePair.split('=');

              if (param[0] === 'ticket') {
                ticket = param[1].substring(1, param[1].length - 1).trim();
                break;
              }
            }
            // a permission ticket must exist in order to send an authorization request
            if (!ticket) {
              throwError(
                () =>
                  new Error(
                    'A permission ticket must exist in order to send an authorization request'
                  )
              );
            }
            // Retry the request with the new token
            return this.authApiService
              .keycloakAuthorize({
                ticket,
                incrementalAuthorization: false,
              })
              .pipe(
                mergeMap((rpt: string) => {
                  const modifiedRequest = req.clone({
                    setHeaders: {
                      Authorization: 'Bearer ' + rpt,
                    },
                  });
                  return next.handle(modifiedRequest);
                }),
                catchError((rptError) => {
                  return throwError(() => rptError);
                })
              );
          } // end if (err.url.indexOf('/token') === -1)
        } // End err.status === 403 || err.status === 401
        return throwError(() => err);
      }) // End catchError
    );
  }
}
