import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import { EMPTY, Observable, throwError } from 'rxjs';
import { catchError, first, map, mergeMap, tap } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { selectGenerateLogsState } from '@generate-logs/selectors/generate-logs.selector';
import { GenerateLogsActions } from '@generate-logs/actions';
import { Exception } from '@generate-logs/reducers/generate-logs.reducer';

@Injectable()
export class UiLogInterceptor implements HttpInterceptor {
  constructor(private store: Store) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return this.store.select(selectGenerateLogsState).pipe(
      first(),
      map((state) => ({
        interaction: state?.latestInteraction,
        logId: state?.latestLog?.id,
      })),
      mergeMap(({ interaction, logId }) => {
        if (!logId) {
          return next.handle(req);
        }
        return next.handle(req).pipe(
          tap((event: HttpEvent<any>) => {
            if (event instanceof HttpResponse) {
              this.store.dispatch(
                GenerateLogsActions.apiRequestSuccess({
                  correlation: { interaction, logId },
                })
              );
            }
          }),
          catchError((error: HttpErrorResponse) => {
            if (error instanceof HttpErrorResponse) {
              switch (error.status) {
                case 500: {
                  const exception: Exception = {
                    exception_message: error.message,
                    exception_class: error.name,
                    stacktrace: error.url,
                  };
                  const correlation = { interaction, logId };
                  this.store.dispatch(
                    GenerateLogsActions.apiRequestFailure({
                      exception,
                      correlation,
                    })
                  );
                  return EMPTY;
                }

                default:
                  return throwError(error);
              }
            } else {
              return throwError(error);
            }
          }) // End catchError
        );
      }) //end mergemap
    );
  }
}
