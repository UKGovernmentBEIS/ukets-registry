import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { AccountAccess } from '@registry-web/auth/auth.model';

@Injectable()
export class MockAuthApiService implements Partial<AuthApiService> {
  hasScope(scopeName: string): Observable<boolean> {
    return of(true);
  }

  retrieveAccessRights(): Observable<AccountAccess[]> {
    return of([]);
  }
}
