import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  ValidationErrors,
} from '@angular/forms';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class ExistingEmitterIdAsyncValidator{
  private existingEmitterIdApiUrl: string;

  constructor(
    @Inject(UK_ETS_REGISTRY_API_BASE_URL) private ukEtsEmitterIdExistsApiBaseUrl: string,
    private http: HttpClient
  ) {
    this.existingEmitterIdApiUrl = `${this.ukEtsEmitterIdExistsApiBaseUrl}/accounts.get.emitter-id`;
  }

  validateEmitterId(operatorId?: number): AsyncValidatorFn {  
    return (control: AbstractControl): Observable<ValidationErrors | null> => {  

      const emitterId = control.value;
      if (!emitterId) return of(null); 

      const params: any = { emitterId };
      if (operatorId) {
        params.operatorIdentifier = operatorId;
      }
      
      return this.http
      .get<boolean>(this.existingEmitterIdApiUrl, {params}).pipe(
          map((response) => {
            return response ? { exists: response } : null;
          }),
          catchError(() => of({ serverError: true }))
        );
    }  
  }
}
