import {
  HttpErrorResponse,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError } from 'rxjs/operators';
import { EMPTY, Observable, throwError } from 'rxjs';
import { Store } from '@ngrx/store';
import { errors } from '@shared/shared.action';

@Injectable()
export class ApiErrorInterceptor implements HttpInterceptor {
  constructor(private store: Store) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
    return next.handle(req).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse) {
          switch (error.status) {
            case 500:
              this.store.dispatch(
                errors({
                  errorSummary: {
                    errors: [
                      {
                        errorMessage:
                          'An unexpected error occurred. Error Id: ' +
                          error.headers.get('X-Request-ID'),
                        componentId: '',
                      },
                    ],
                  },
                })
              );
              return EMPTY;
            default:
              return throwError(error);
          }
        } else {
          return throwError(error);
        }
      })
    );
  }
}
