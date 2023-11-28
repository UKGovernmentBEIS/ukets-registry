import { Pipe, PipeTransform } from '@angular/core';
import { Observable } from 'rxjs';
import { ARAccessRights } from '@shared/model/account';
import { AccountAccessService } from '../../auth/account-access.service';

@Pipe({
  name: 'accountAccess'
})
export class AccountAccessPipe implements PipeTransform {
  constructor(private accountAccessService: AccountAccessService) {}

  transform(
    accountFullIdentifier: string,
    accessRight: ARAccessRights
  ): Observable<boolean> {
    return this.accountAccessService.isArWithAccessRight(
      accountFullIdentifier,
      accessRight
    );
  }
}
