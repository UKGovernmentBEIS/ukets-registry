import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import {Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { DebounceClickService } from '../debounce-click.service';

@Injectable()
export class DebounceClickInterceptor implements HttpInterceptor {
  constructor(private debounceService: DebounceClickService){}
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const submitUuid = sessionStorage.getItem('submitUuid');
    if (!submitUuid) {
      return next.handle(req);
    }
    return next.handle(req).pipe(
      tap((event: HttpEvent<any>) => {
        if (event instanceof HttpResponse) {
          this.updateStatus('completed', submitUuid);
        }
      }),
      catchError((error: HttpErrorResponse) => {
       if(error.status != 401) {
        this.updateStatus('failed', submitUuid);
       }
        return throwError(error);
      }))
     }

     private updateStatus(status: string, uuid: string) {
      const submitAction = {status: status, uuid: uuid};
      this.debounceService.updateStatus(submitAction);
      sessionStorage.removeItem('submitUuid');
     }
}
