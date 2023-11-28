import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ARAccessRights } from '@shared/model/account';
import { mergeMap } from 'rxjs/operators';
import { AccountAccess } from './auth.model';
import { AuthApiService } from '@registry-web/auth/auth-api.service';

@Injectable()
export class AccountAccessService {
  constructor(private readonly authApiService: AuthApiService) {}

  isArWithAccessRight(
    accountFullIdentifier: string,
    accessRight: ARAccessRights
  ): Observable<boolean> {
    return this.authApiService.retrieveAccessRights().pipe(
      mergeMap((accountAccesses: AccountAccess[]) => {
        if (accountFullIdentifier) {
          return of(
            this.containsAccessRight(
              accountAccesses.find(
                (ac) => ac.accountFullIdentifier === accountFullIdentifier
              ),
              accessRight
            )
          );
        } else {
          return of(false);
        }
      })
    );
  }

  containsAccessRight(
    accountAccess: AccountAccess,
    rightToCheck: ARAccessRights
  ): boolean {
    if (!accountAccess) {
      return false;
    }
    switch (accountAccess.accessRight) {
      case ARAccessRights.INITIATE_AND_APPROVE: {
        return true;
      }
      case ARAccessRights.INITIATE: {
        return ![
          ARAccessRights.INITIATE_AND_APPROVE,
          ARAccessRights.APPROVE,
        ].includes(rightToCheck);
      }
      case ARAccessRights.APPROVE: {
        return ![
          ARAccessRights.INITIATE_AND_APPROVE,
          ARAccessRights.INITIATE,
        ].includes(rightToCheck);
      }
      case ARAccessRights.SURRENDER_INITIATE_AND_APPROVE: {
        return rightToCheck === ARAccessRights.SURRENDER_INITIATE_AND_APPROVE;
      }
      case ARAccessRights.READ_ONLY: {
        return rightToCheck === ARAccessRights.READ_ONLY;
      }
      case ARAccessRights.ROLE_BASED: {
        return rightToCheck === ARAccessRights.ROLE_BASED;
      }
    }
  }
}
