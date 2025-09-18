import { Injectable } from '@angular/core';
import { combineLatest, Observable, of } from 'rxjs';
import { AccountStatus, ARAccessRights } from '@shared/model/account';
import { map } from 'rxjs/operators';
import { AccountAccessService } from '../../auth/account-access.service';

@Injectable()
export class AccountBusinessRulesService {
  constructor(private accountAccessService: AccountAccessService) {}

  //TODO - Move the button presentation business logic to the backend corresponding services.

  public canRequestUpdate(
    isAdmin: boolean,
    isReadOnlyAdmin: boolean,
    accountIsGovernment: boolean,
    accountStatus: AccountStatus,
    displayButtonForAccountInStatusProposed: boolean,
    accountFullIdentifier: string,
    accessRights: ARAccessRights,
    displayButtonForAccountInStatusClosurePending: boolean
  ): Observable<boolean> {
    if (accountIsGovernment) {
      return of(false);
    }
    return combineLatest([
      this.isAdminAndCanRequestUpdate(
        isAdmin,
        isReadOnlyAdmin,
        accountStatus,
        displayButtonForAccountInStatusProposed,
        displayButtonForAccountInStatusClosurePending
      ),
      this.isArAndCanRequestUpdate(
        accountFullIdentifier,
        accountStatus,
        accessRights
      ),
    ]).pipe(map((bools) => bools.some((a) => a)));
  }

  private isAdminAndCanRequestUpdate(
    isAdmin: boolean,
    isReadOnlyAdmin: boolean,
    accountStatus: AccountStatus,
    displayButtonForAccountInStatusProposed: boolean,
    displayButtonForAccountInStatusClosurePending: boolean
  ): Observable<boolean> {
    if (isAdmin) {
      if (
        isReadOnlyAdmin ||
        accountStatus === 'CLOSED' ||
        (accountStatus === 'CLOSURE_PENDING' &&
          !displayButtonForAccountInStatusClosurePending) ||
        (accountStatus === 'PROPOSED' &&
          !displayButtonForAccountInStatusProposed)
      ) {
        return of(false);
      } else {
        return of(true);
      }
    }
    return of(false);
  }

  private isArAndCanRequestUpdate(
    accountFullIdentifier: string,
    accountStatus: AccountStatus,
    menuItemAccessRight: ARAccessRights
  ): Observable<boolean> {
    return this.accountAccessService
      .isArWithAccessRight(accountFullIdentifier, menuItemAccessRight)
      .pipe(
        map((hasAccessRight) => {
          if (!hasAccessRight) {
            return false;
          } else {
            return [
              'OPEN',
              'ALL_TRANSACTIONS_RESTRICTED',
              'SOME_TRANSACTIONS_RESTRICTED',
            ].includes(accountStatus);
          }
        })
      );
  }
}
