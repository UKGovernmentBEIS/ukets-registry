import { Pipe, PipeTransform } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { AccountStatus, ARAccessRights } from '@shared/model/account';
import { AccountBusinessRulesService } from '@account-management/service/account-business-rules.service';
import { map } from 'rxjs/operators';

@Pipe({
  name: 'canRequestUpdate',
})
export class CanRequestUpdatePipe implements PipeTransform {
  constructor(
    private accountBusinessRulesService: AccountBusinessRulesService
  ) {}

  transform(
    isAdmin: boolean,
    isReadOnlyAdmin: boolean,
    accountIsGovernment: boolean,
    accountStatus: AccountStatus,
    displayButtonForAccountInStatusProposed: boolean,
    accountFullIdentifier: string,
    accessRight: ARAccessRights,
    hideForReadAccessOnly: boolean,
    displayButtonForAccountInStatusClosurePending?: boolean
  ): Observable<boolean> {
    if (hideForReadAccessOnly) {
      return this.combineRights(
        isAdmin,
        isReadOnlyAdmin,
        accountIsGovernment,
        accountStatus,
        displayButtonForAccountInStatusProposed,
        accountFullIdentifier,
        displayButtonForAccountInStatusClosurePending
      );
    }

    return this.accountBusinessRulesService.canRequestUpdate(
      isAdmin,
      isReadOnlyAdmin,
      accountIsGovernment,
      accountStatus,
      displayButtonForAccountInStatusProposed,
      accountFullIdentifier,
      accessRight,
      displayButtonForAccountInStatusClosurePending
    );
  }
  combineRights(
    isAdmin: boolean,
    isReadOnlyAdmin: boolean,
    accountIsGovernment: boolean,
    accountStatus: AccountStatus,
    displayButtonForAccountInStatusProposed: boolean,
    accountFullIdentifier: string,
    displayButtonForAccountInStatusClosurePending: boolean
  ) {
    return combineLatest([
      this.accountBusinessRulesService.canRequestUpdate(
        isAdmin,
        isReadOnlyAdmin,
        accountIsGovernment,
        accountStatus,
        displayButtonForAccountInStatusProposed,
        accountFullIdentifier,
        ARAccessRights.INITIATE,
        displayButtonForAccountInStatusClosurePending
      ),
      this.accountBusinessRulesService.canRequestUpdate(
        isAdmin,
        isReadOnlyAdmin,
        accountIsGovernment,
        accountStatus,
        displayButtonForAccountInStatusProposed,
        accountFullIdentifier,
        ARAccessRights.APPROVE,
        displayButtonForAccountInStatusClosurePending
      ),
    ]).pipe(map((bools) => bools.some((a) => a)));
  }
}
