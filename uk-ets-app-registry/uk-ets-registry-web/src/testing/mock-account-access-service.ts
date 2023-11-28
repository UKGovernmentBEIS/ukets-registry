import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ARAccessRights } from '@shared/model/account';
import { AccountAccess } from '@registry-web/auth/auth.model';

@Injectable()
export class MockAccountAccessService {
  isArWithAccessRight(
    accountFullIdentifier: string,
    accessRight: ARAccessRights
  ): Observable<boolean> {
    return of(true);
  }

  containsAccessRight(
    accountAccess: AccountAccess,
    rightToCheck: ARAccessRights
  ): boolean {
    return true;
  }
}
